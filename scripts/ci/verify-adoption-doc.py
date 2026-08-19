#!/usr/bin/env python3
"""
verify-adoption-doc.py — drift gate for adoption docs.

Extracts every executable ```bash block from an adoption doc, runs each,
fails the run if any block exits non-zero. This makes the doc's
verify blocks the SINGLE SOURCE OF TRUTH for the adoption contract —
if the implementation drifts (file renamed, dimension removed, alias
moved), the corresponding verify breaks and the PR fails until either
the implementation is reverted or the doc is updated to match.

Blocks are extracted only when their fenced ```bash region appears
under one of these header patterns (configurable):

  ### ✅ Verify                     ← consumer.md
  ### Release-time check (CI)       ← library.md
  #### [§N — …]                    ← ADOPTION_KMP_PRODUCT_FLAVORS.md
                                       (Tier 2 per-version sections)

To skip a specific block without removing it from the doc, prefix the
first line with `# adoption-verify: skip`.

Usage:
  scripts/ci/verify-adoption-doc.py docs/adoption/v2.7/library.md
  scripts/ci/verify-adoption-doc.py docs/adoption/v2.7/library.md docs/ADOPTION_KMP_PRODUCT_FLAVORS.md
  scripts/ci/verify-adoption-doc.py --strict docs/...   (fail on any block error, not just exit code)

Exit codes:
  0 — all blocks passed
  1 — one or more blocks failed
  2 — usage error
"""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import textwrap
from pathlib import Path
from typing import List, Tuple

# Headers that mark a section whose immediately-following ```bash block
# is an executable verify gate.
HEADER_PATTERNS = [
    r"^###\s*✅\s*Verify",
    r"^###\s*Release-time check",
    r"^####\s*\[§\d+",
]

# Compile once
_HEADER_RE = re.compile("|".join(f"(?:{p})" for p in HEADER_PATTERNS), re.MULTILINE)

# Track the most recent H2 so failure messages name the section.
_H2_RE = re.compile(r"^##\s+([^\n]+)$", re.MULTILINE)


def extract_blocks(doc_path: Path) -> List[Tuple[str, str, str]]:
    """
    Return list of (h2_section, verify_header, bash_code) tuples.
    """
    text = doc_path.read_text(encoding="utf-8")
    blocks: List[Tuple[str, str, str]] = []

    # Iterate header matches; for each, look forward for the next ```bash
    # block until either end-of-doc or the next H2 (whichever is sooner).
    header_matches = list(_HEADER_RE.finditer(text))
    h2_matches = list(_H2_RE.finditer(text))

    def latest_h2_before(pos: int) -> str:
        last = ""
        for m in h2_matches:
            if m.start() < pos:
                last = m.group(1).strip()
        return last or "(no preceding ## section)"

    for i, hm in enumerate(header_matches):
        # Boundary: next header or EOF
        end = header_matches[i + 1].start() if i + 1 < len(header_matches) else len(text)
        section_text = text[hm.start():end]

        # Find the first fenced ```bash block in this region
        bash_match = re.search(r"```bash\n(.*?)\n```", section_text, re.DOTALL)
        if not bash_match:
            continue

        code = bash_match.group(1)
        first_line = code.split("\n", 1)[0].strip()
        if first_line.startswith("# adoption-verify: skip"):
            continue

        h2 = latest_h2_before(hm.start())
        verify_header = section_text.split("\n", 1)[0].strip()
        blocks.append((h2, verify_header, code))

    return blocks


def run_block(code: str, cwd: Path) -> Tuple[int, str]:
    """Execute a bash block, return (exit_code, combined_output)."""
    proc = subprocess.run(
        ["bash", "-c", code],
        cwd=str(cwd),
        capture_output=True,
        text=True,
        env={**__import__("os").environ, "LC_ALL": "C.UTF-8"},
    )
    output = proc.stdout + proc.stderr
    return proc.returncode, output


def main() -> int:
    ap = argparse.ArgumentParser(
        description="Run every ✅ Verify / Release-time check / §N block in an adoption doc.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=textwrap.dedent(
            """\
            Examples:
              scripts/ci/verify-adoption-doc.py docs/adoption/v2.7/library.md
              scripts/ci/verify-adoption-doc.py --cwd samples/kmp-project-template \\
                  samples/kmp-project-template/docs/ADOPTION_KMP_PRODUCT_FLAVORS.md
            """
        ),
    )
    ap.add_argument("docs", nargs="+", type=Path, help="adoption doc path(s)")
    ap.add_argument(
        "--cwd",
        type=Path,
        default=Path.cwd(),
        help="working directory for executing blocks (default: cwd)",
    )
    ap.add_argument(
        "--max-output",
        type=int,
        default=2000,
        help="max chars of failing-block output to print (default: 2000)",
    )
    args = ap.parse_args()

    if not args.cwd.exists():
        print(f"--cwd path does not exist: {args.cwd}", file=sys.stderr)
        return 2

    overall_pass = 0
    overall_fail = 0
    overall_skip = 0
    failures: List[Tuple[Path, str, str, str, str]] = []  # (doc, h2, verify_hdr, code, out)

    for doc_path in args.docs:
        if not doc_path.exists():
            print(f"✗ doc not found: {doc_path}", file=sys.stderr)
            return 2

        print(f"\n━━━ {doc_path} ━━━")
        blocks = extract_blocks(doc_path)
        if not blocks:
            print("  (no verify blocks found)")
            continue

        for h2, verify_hdr, code in blocks:
            label = f"  · {h2}"
            print(label)
            rc, out = run_block(code, args.cwd)
            if rc == 0:
                overall_pass += 1
                print("    ✓ PASS")
            else:
                overall_fail += 1
                failures.append((doc_path, h2, verify_hdr, code, out))
                print(f"    ✗ FAIL (exit {rc})")

    total = overall_pass + overall_fail + overall_skip
    print(f"\n{'═' * 60}")
    print(f"Total: {total}   PASS: {overall_pass}   FAIL: {overall_fail}")

    if failures:
        print("\n┄ Drift detected — the following verify blocks failed: ┄\n")
        for doc, h2, verify_hdr, code, out in failures:
            print(f"┌── {doc} ── §{h2} ── {verify_hdr}")
            print(textwrap.indent(code.rstrip(), "│  "))
            print(f"│")
            print("│  Output:")
            trimmed = out.strip()
            if len(trimmed) > args.max_output:
                trimmed = trimmed[: args.max_output] + "\n... (truncated)"
            print(textwrap.indent(trimmed or "(no output)", "│  "))
            print("└─" + "─" * 60 + "\n")

        print(
            "Fix path: either revert the implementation change that caused drift, "
            "or update the adoption doc's verify block + 'What you should have' "
            "section to match the new implementation reality. Do not silence the "
            "drift gate by removing the verify block — that defeats the single-"
            "source-of-truth contract.",
            file=sys.stderr,
        )
        return 1

    return 0


if __name__ == "__main__":
    sys.exit(main())
