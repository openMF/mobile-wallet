#!/bin/bash
#
# KMP Project Master Setup Script
# Orchestrates the complete project setup process
#

# Colors and formatting
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Emoji indicators
CHECK_MARK="✅"
WARNING="⚠️"
ROCKET="🚀"
GEAR="⚙️"
FIRE="🔥"
PACKAGE="📦"
KEY="🔑"
CLEAN="🧹"

# Exit on any error
set -e

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Print section header
print_section() {
    echo
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC} ${BOLD}$1${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
    echo
}

# Print step header
print_step() {
    echo
    echo -e "${PURPLE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${PURPLE}${ROCKET} STEP $1: $2${NC}"
    echo -e "${PURPLE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo
}

# Print success message
print_success() {
    echo -e "${GREEN}${CHECK_MARK} $1${NC}"
}

# Print warning message
print_warning() {
    echo -e "${YELLOW}${WARNING} $1${NC}"
}

# Print info message
print_info() {
    echo -e "${CYAN}${GEAR} $1${NC}"
}

# Print error message
print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Print welcome banner
print_welcome_banner() {
    clear
    echo -e "${BLUE}"
    cat << "EOF"
╔════════════════════════════════════════════════════════════════════════╗
║                                                                        ║
║                  🚀 KMP PROJECT MASTER SETUP 🚀                        ║
║                                                                        ║
║            Complete Kotlin Multiplatform Project Setup                ║
║                                                                        ║
╚════════════════════════════════════════════════════════════════════════╝
EOF
    echo -e "${NC}"
    echo
    echo -e "${CYAN}This script will guide you through the complete setup process:${NC}"
    echo -e "  1. ${PACKAGE} Project Customization (Package & Names)"
    echo -e "  2. ${FIRE} Firebase Project Setup"
    echo -e "  3. ${KEY} Keystore Generation & Secrets Management"
    echo -e "  4. ${CHECK_MARK} Verification & Summary"
    echo
}

# Validate prerequisites
validate_prerequisites() {
    print_section "Validating Prerequisites"
    
    local missing_tools=()
    
    # Check for required tools
    if ! command -v bash &> /dev/null; then
        missing_tools+=("bash")
    fi
    
    # Check bash version (need 4+)
    if [[ ${BASH_VERSINFO[0]} -lt 4 ]]; then
        print_error "Bash version 4+ required. Current version: ${BASH_VERSINFO[0]}"
        missing_tools+=("bash 4+")
    fi
    
    if ! command -v keytool &> /dev/null; then
        missing_tools+=("keytool (Java JDK)")
    fi
    
    if ! command -v firebase &> /dev/null; then
        print_warning "Firebase CLI not found. It will be needed for Step 2."
        echo -e "${CYAN}Install with: npm install -g firebase-tools${NC}"
        missing_tools+=("firebase-tools (optional)")
    fi
    
    if ! command -v gh &> /dev/null; then
        print_warning "GitHub CLI not found. It will be needed for GitHub secrets management."
        echo -e "${CYAN}Install from: https://cli.github.com/${NC}"
        missing_tools+=("gh (optional)")
    fi
    
    if [ ${#missing_tools[@]} -gt 0 ]; then
        echo
        print_warning "Some tools are missing. You can continue, but some steps may fail."
        echo
        read -p "Do you want to continue? (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "Setup cancelled. Please install the missing tools and try again."
            exit 1
        fi
    else
        print_success "All prerequisites validated"
    fi
}

# Collect user inputs
collect_inputs() {
    print_section "Project Configuration"
    
    echo -e "${CYAN}Please provide the following information:${NC}"
    echo
    
    # Package name
    while true; do
        read -p "$(echo -e ${CYAN}Enter Android package name ${BOLD}[e.g., com.example.myapp]${NC}: )" PACKAGE_NAME
        if [[ $PACKAGE_NAME =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
            break
        else
            print_error "Invalid package name format. Use lowercase with dots (e.g., com.example.app)"
        fi
    done
    
    # Project name
    read -p "$(echo -e ${CYAN}Enter project name ${BOLD}[e.g., MyAwesomeApp]${NC}: )" PROJECT_NAME
    while [ -z "$PROJECT_NAME" ]; do
        print_error "Project name cannot be empty"
        read -p "$(echo -e ${CYAN}Enter project name: ${NC})" PROJECT_NAME
    done
    
    # App name (optional, defaults to project name)
    read -p "$(echo -e ${CYAN}Enter application display name ${BOLD}[default: $PROJECT_NAME]${NC}: )" APP_NAME
    APP_NAME=${APP_NAME:-$PROJECT_NAME}
    
    # iOS Bundle ID
    read -p "$(echo -e ${CYAN}Enter iOS bundle identifier ${BOLD}[default: $PACKAGE_NAME]${NC}: )" IOS_BUNDLE_ID
    IOS_BUNDLE_ID=${IOS_BUNDLE_ID:-$PACKAGE_NAME}
    
    # Firebase Project ID
    echo
    print_info "Firebase Project ID should be globally unique and lowercase with hyphens"
    read -p "$(echo -e ${CYAN}Enter Firebase project ID ${BOLD}[e.g., my-app-12345]${NC}: )" FIREBASE_PROJECT_ID
    while [ -z "$FIREBASE_PROJECT_ID" ]; do
        print_error "Firebase project ID cannot be empty"
        read -p "$(echo -e ${CYAN}Enter Firebase project ID: ${NC})" FIREBASE_PROJECT_ID
    done
    
    # Company/Organization info for keystores
    echo
    print_info "Keystore certificate information (for signing your app)"
    read -p "$(echo -e ${CYAN}Company/Organization name: ${NC})" COMPANY_NAME
    read -p "$(echo -e ${CYAN}Department/Team name ${BOLD}[optional]${NC}: )" DEPARTMENT
    read -p "$(echo -e ${CYAN}City: ${NC})" CITY
    read -p "$(echo -e ${CYAN}State/Province: ${NC})" STATE
    read -p "$(echo -e ${CYAN}Country code ${BOLD}[e.g., US, IN, UK]${NC}: )" COUNTRY
    
    # Keystore passwords
    echo
    print_info "Keystore passwords (keep these safe!)"
    read -sp "$(echo -e ${CYAN}Enter keystore password: ${NC})" KEYSTORE_PASSWORD
    echo
    read -sp "$(echo -e ${CYAN}Confirm keystore password: ${NC})" KEYSTORE_PASSWORD_CONFIRM
    echo
    
    while [ "$KEYSTORE_PASSWORD" != "$KEYSTORE_PASSWORD_CONFIRM" ]; do
        print_error "Passwords do not match"
        read -sp "$(echo -e ${CYAN}Enter keystore password: ${NC})" KEYSTORE_PASSWORD
        echo
        read -sp "$(echo -e ${CYAN}Confirm keystore password: ${NC})" KEYSTORE_PASSWORD_CONFIRM
        echo
    done
    
    read -p "$(echo -e ${CYAN}Enter key alias ${BOLD}[default: $PROJECT_NAME]${NC}: )" KEY_ALIAS
    KEY_ALIAS=${KEY_ALIAS:-$PROJECT_NAME}
    
    # Confirm inputs
    echo
    print_section "Configuration Summary"
    echo -e "${CYAN}Please review your configuration:${NC}"
    echo
    echo -e "  ${BOLD}Package Name:${NC} $PACKAGE_NAME"
    echo -e "  ${BOLD}Project Name:${NC} $PROJECT_NAME"
    echo -e "  ${BOLD}App Display Name:${NC} $APP_NAME"
    echo -e "  ${BOLD}iOS Bundle ID:${NC} $IOS_BUNDLE_ID"
    echo -e "  ${BOLD}Firebase Project:${NC} $FIREBASE_PROJECT_ID"
    echo -e "  ${BOLD}Company:${NC} $COMPANY_NAME"
    echo -e "  ${BOLD}City:${NC} $CITY"
    echo -e "  ${BOLD}State:${NC} $STATE"
    echo -e "  ${BOLD}Country:${NC} $COUNTRY"
    echo -e "  ${BOLD}Key Alias:${NC} $KEY_ALIAS"
    echo
    
    read -p "$(echo -e ${YELLOW}Is this correct? ${BOLD}[y/n]${NC}: )" -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Setup cancelled. Please run the script again."
        exit 0
    fi
}

# Write keystore DN identity to gradle/fork.properties (non-secret values).
# Keystore passwords are written to secrets/live/android/keystores/ per-value files.
# This replaces the retired create_secrets_env() function that wrote secrets.env.
setup_keystore_config() {
    print_info "Writing keystore DN to gradle/fork.properties"

    local FORK_PROPS="gradle/fork.properties"

    # Copy template if not already present
    if [ ! -f "$FORK_PROPS" ] && [ -f "gradle/fork.properties.template" ]; then
        cp "gradle/fork.properties.template" "$FORK_PROPS"
        print_info "Copied fork.properties.template → fork.properties"
    fi

    if [ -f "$FORK_PROPS" ]; then
        # Update existing keys (or append if absent)
        for key_pair in \
            "keystore.dn.org_unit=${DEPARTMENT:-Mobile}" \
            "keystore.dn.city=${CITY}" \
            "keystore.dn.state=${STATE}" \
            "keystore.dn.country=${COUNTRY:-US}"; do
            local k="${key_pair%%=*}"
            local v="${key_pair#*=}"
            if grep -q "^${k}=" "$FORK_PROPS"; then
                # Replace existing line (macOS-safe sed using a temp file)
                local tmp
                tmp=$(mktemp)
                sed "s|^${k}=.*|${k}=${v}|" "$FORK_PROPS" > "$tmp" && mv "$tmp" "$FORK_PROPS"
            else
                echo "${k}=${v}" >> "$FORK_PROPS"
            fi
        done
        print_success "Updated keystore DN keys in $FORK_PROPS"
    else
        print_warning "gradle/fork.properties not found — DN keys not written. Fill them in manually."
    fi

    # Write keystore passwords to per-value secret files
    local KEYSTORE_SECRETS_DIR="secrets/live/android/keystores"
    mkdir -p "$KEYSTORE_SECRETS_DIR"
    printf '%s' "$KEYSTORE_PASSWORD" > "$KEYSTORE_SECRETS_DIR/keystore_password"
    printf '%s' "$KEY_ALIAS"         > "$KEYSTORE_SECRETS_DIR/keystore_alias"
    printf '%s' "$KEYSTORE_PASSWORD" > "$KEYSTORE_SECRETS_DIR/keystore_alias_password"
    print_success "Keystore passwords written to $KEYSTORE_SECRETS_DIR/"
    print_info "To sync secrets to GitHub run: scripts/secrets/sync-secrets-to-github.sh"
}

# Step 1: Run customizer
run_customizer() {
    print_step "1" "Project Customization"
    
    if [ ! -f "$SCRIPT_DIR/customizer.sh" ]; then
        print_error "customizer.sh not found"
        exit 1
    fi
    
    print_info "Customizing project with your package name and project name"
    bash "$SCRIPT_DIR/customizer.sh" "$PACKAGE_NAME" "$PROJECT_NAME" "$APP_NAME"
    
    print_success "Project customization completed"
    
    echo
    read -p "Press Enter to continue to Firebase setup..."
}

# Step 2: Run Firebase setup
run_firebase_setup() {
    print_step "2" "Firebase Project Setup"
    
    if [ ! -f "$SCRIPT_DIR/firebase-setup.sh" ]; then
        print_error "firebase-setup.sh not found"
        exit 1
    fi
    
    # Make script executable
    chmod +x "$SCRIPT_DIR/firebase-setup.sh"
    
    print_info "Setting up Firebase project and registering apps"
    print_info "Android package will be read from gradle/libs.versions.toml"
    
    # Ask if user wants to run Firebase setup
    echo -e "${YELLOW}Firebase setup requires Firebase CLI to be installed and authenticated.${NC}"
    read -p "Do you want to run Firebase setup now? (y/n): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        bash "$SCRIPT_DIR/firebase-setup.sh" "$FIREBASE_PROJECT_ID" "$IOS_BUNDLE_ID" || {
            print_warning "Firebase setup encountered issues. You can run it manually later."
            print_info "Run: bash firebase-setup.sh $FIREBASE_PROJECT_ID $IOS_BUNDLE_ID"
        }
    else
        print_warning "Skipping Firebase setup. You can run it manually later:"
        print_info "bash firebase-setup.sh $FIREBASE_PROJECT_ID $IOS_BUNDLE_ID"
    fi
    
    echo
    read -p "Press Enter to continue to keystore generation..."
}

# Step 3: Run keystore manager
run_keystore_generation() {
    print_step "3" "Keystore Generation & Secrets Management"
    
    if [ ! -f "$SCRIPT_DIR/keystore-manager.sh" ]; then
        print_error "keystore-manager.sh not found"
        exit 1
    fi
    
    # Make script executable
    chmod +x "$SCRIPT_DIR/keystore-manager.sh"
    
    print_info "Generating Android keystores and configuring secrets"
    bash "$SCRIPT_DIR/keystore-manager.sh" generate
    
    print_success "Keystore generation completed"
    
    echo
    print_info "You can view your secrets with: bash keystore-manager.sh view"
    
    echo
    read -p "Press Enter to continue..."
}

# Step 4: Optional iOS Setup
run_ios_setup() {
    print_step "4" "iOS Setup (Optional)"

    echo -e "${CYAN}iOS deployment requires:${NC}"
    echo "  • macOS with Xcode"
    echo "  • Apple Developer Account (\$99/year)"
    echo "  • App Store Connect API key"
    echo "  • Match repository for code signing"
    echo
    echo -e "${YELLOW}You can configure iOS now or run the setup wizard later:${NC}"
    echo -e "  ${BLUE}bash scripts/ios/setup_ios_complete.sh${NC}"
    echo

    read -p "Configure iOS deployment now? [y/N]: " -n 1 -r
    echo

    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Skipping iOS setup"
        print_info "Run later: bash scripts/ios/setup_ios_complete.sh"
        return 0
    fi

    # Check if setup script exists
    if [ ! -f "$SCRIPT_DIR/scripts/ios/setup_ios_complete.sh" ]; then
        print_warning "iOS setup script not found at scripts/ios/setup_ios_complete.sh"
        return 0
    fi

    # Make executable
    chmod +x "$SCRIPT_DIR/scripts/ios/setup_ios_complete.sh"

    print_info "Launching iOS setup wizard..."
    echo

    # Run iOS setup
    bash "$SCRIPT_DIR/scripts/ios/setup_ios_complete.sh" || {
        print_warning "iOS setup encountered issues"
        print_info "You can run it manually later: bash scripts/ios/setup_ios_complete.sh"
    }
}

# Print final summary and next steps
print_final_summary() {
    print_step "5" "Setup Complete - Next Steps"
    
    echo -e "${GREEN}${ROCKET} Congratulations! Your KMP project setup is complete!${NC}"
    echo
    
    print_section "What Was Configured"
    echo -e "${CYAN}✓ Project Structure${NC}"
    echo -e "  - Package name updated to: ${BOLD}$PACKAGE_NAME${NC}"
    echo -e "  - Project renamed to: ${BOLD}$PROJECT_NAME${NC}"
    echo -e "  - All code files and configurations updated"
    echo
    echo -e "${CYAN}✓ Firebase Configuration${NC}"
    echo -e "  - Firebase project: ${BOLD}$FIREBASE_PROJECT_ID${NC}"
    echo -e "  - Android & iOS apps registered"
    echo -e "  - Configuration files downloaded"
    echo
    echo -e "${CYAN}✓ Keystore & Security${NC}"
    echo -e "  - Upload keystore generated"
    echo -e "  - Configuration files updated with keystore info"
    echo -e "  - Keystore DN written to gradle/fork.properties (non-secret)"
    echo -e "  - Keystore passwords written to secrets/live/android/keystores/ (gitignored)"
    echo

    print_section "Important Files Created"
    echo -e "  ${BOLD}keystores/${NC}"
    echo -e "    └── upload_keystore.keystore  ${YELLOW}(Keep this secure!)${NC}"
    echo
    echo -e "  ${BOLD}secrets/live/android/keystores/${NC}  ${CYAN}(Keystore passwords — gitignored)${NC}"
    echo -e "    ├── keystore_password"
    echo -e "    ├── keystore_alias"
    echo -e "    └── keystore_alias_password"
    echo
    echo -e "  ${BOLD}gradle/fork.properties${NC}  ${CYAN}(Keystore DN identity + org config — gitignored)${NC}"
    echo
    echo -e "  ${BOLD}cmp-android/${NC}"
    echo -e "    └── google-services.json"
    echo
    echo -e "  ${BOLD}cmp-ios/iosApp/${NC}"
    echo -e "    └── GoogleService-Info.plist"
    echo
    
    print_section "Next Steps"
    echo -e "${CYAN}1. ${BOLD}Complete Firebase Configuration${NC}"
    echo -e "   - Visit: ${BLUE}https://console.firebase.google.com/project/$FIREBASE_PROJECT_ID${NC}"
    echo -e "   - Enable Authentication, Firestore, or other services you need"
    echo -e "   - Set up Firebase App Distribution groups"
    echo -e "   - Download service account key: ${BOLD}firebaseAppDistributionServiceCredentialsFile.json${NC}"
    echo -e "   - Place in: ${BOLD}secrets/${NC} directory"
    echo
    echo -e "${CYAN}2. ${BOLD}Add Additional Secret Files${NC}"
    echo -e "   Place these files in the ${BOLD}secrets/${NC} directory (if needed):"
    echo -e "   - ${BOLD}playStorePublishServiceCredentialsFile.json${NC} (for Play Store deployment)"
    echo -e "   - ${BOLD}Auth_key.p8${NC} (for iOS App Store Connect)"
    echo -e "   - ${BOLD}match_ci_key${NC} (for iOS code signing)"
    echo
    echo -e "   Then run: ${CYAN}bash keystore-manager.sh encode-secrets${NC}"
    echo
    echo -e "${CYAN}3. ${BOLD}iOS Setup${NC}"
    echo -e "   - Open ${BOLD}cmp-ios/iosApp.xcodeproj${NC} in Xcode"
    echo -e "   - Add ${BOLD}GoogleService-Info.plist${NC} to the project"
    echo -e "   - Configure signing & capabilities"
    echo
    echo -e "${CYAN}4. ${BOLD}Test Your Build${NC}"
    echo -e "   Android: ${CYAN}./gradlew :cmp-android:assembleProdRelease${NC}"
    echo -e "   iOS: Open Xcode and build the project"
    echo
    echo -e "${CYAN}5. ${BOLD}Set Up CI/CD (Optional)${NC}"
    echo -e "   To push secrets to GitHub:"
    echo -e "   ${CYAN}bash scripts/secrets/sync-secrets-to-github.sh --repo=username/repo${NC}"
    echo
    echo -e "${CYAN}6. ${BOLD}Version Control${NC}"
    echo -e "   ⚠️  ${YELLOW}NEVER commit these files:${NC}"
    echo -e "     - keystores/*.keystore"
    echo -e "     - secrets/live/android/keystores/*"
    echo -e "     - secrets/*"
    echo
    echo -e "   ${GREEN}Safe to commit:${NC}"
    echo -e "     - cmp-android/google-services.json"
    echo -e "     - All other configuration files"
    echo
    
    print_section "Useful Commands"
    echo -e "  ${CYAN}View secrets:${NC}        bash keystore-manager.sh view"
    echo -e "  ${CYAN}Encode secrets:${NC}      bash keystore-manager.sh encode-secrets"
    echo -e "  ${CYAN}Add to GitHub:${NC}       bash keystore-manager.sh add --repo=user/repo"
    echo -e "  ${CYAN}List GitHub secrets:${NC} bash keystore-manager.sh list --repo=user/repo"
    echo
    
    print_section "Documentation & Support"
    echo -e "  ${CYAN}Fastlane Config:${NC}   fastlane-config/project_config.rb"
    echo -e "  ${CYAN}Gradle Config:${NC}     cmp-android/build.gradle.kts"
    echo -e "  ${CYAN}Version Catalog:${NC}   gradle/libs.versions.toml"
    echo
    
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                                ║${NC}"
    echo -e "${GREEN}║  ${BOLD}${ROCKET} Your KMP project is ready for development! ${ROCKET}${GREEN}            ║${NC}"
    echo -e "${GREEN}║                                                                ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
    echo
}

# Save configuration for reference
save_configuration() {
    local config_file="PROJECT_SETUP_INFO.txt"
    
    cat > "$config_file" << EOF
# Project Setup Configuration
# Generated: $(date)

## Project Information
Package Name: $PACKAGE_NAME
Project Name: $PROJECT_NAME
App Display Name: $APP_NAME
iOS Bundle ID: $IOS_BUNDLE_ID

## Firebase
Project ID: $FIREBASE_PROJECT_ID
Console URL: https://console.firebase.google.com/project/$FIREBASE_PROJECT_ID

## Keystore
Key Alias: $KEY_ALIAS
Original Keystore: keystores/original.keystore
Upload Keystore: keystores/upload.keystore

## Company Information
Company: $COMPANY_NAME
Department: $DEPARTMENT
City: $CITY
State: $STATE
Country: $COUNTRY

## Important Notes
- Keep keystore files and passwords secure
- Never commit keystores or secrets/live/android/keystores/* to version control
- Add required secret files to secrets/ directory
- Run 'bash scripts/secrets/sync-secrets-to-github.sh' to push secrets to GitHub
EOF
    
    print_success "Configuration saved to $config_file"
}

# Main execution
main() {
    print_welcome_banner
    
    # Validate prerequisites
    validate_prerequisites
    
    # Collect user inputs
    collect_inputs
    
    # Write keystore DN to fork.properties + passwords to secrets/live/android/keystores/
    setup_keystore_config

    # Step 1: Customization
    run_customizer

    # Step 2: Firebase setup
    run_firebase_setup

    # Step 3: Keystore generation
    run_keystore_generation

    # Step 4: iOS setup (optional)
    run_ios_setup

    # Save configuration
    save_configuration
    
    # Final summary
    print_final_summary
}

# Handle script interruption
trap 'echo -e "\n${RED}Setup interrupted. You can run the script again to continue.${NC}"; exit 1' INT TERM

# Execute main function
main
