# Apple Push Notifications (APN) Auth Key — Optional

Used for: enabling iOS push notifications via Firebase Cloud Messaging (FCM).
This is **optional** — skip it entirely if your app doesn't use push notifications.

## How to get it

1. Go to [Apple Developer — Keys](https://developer.apple.com/account/resources/authkeys/list)
2. Click **+** to create a new key
3. Name: `APNs Auth Key`
4. Check **Apple Push Notifications service (APNs)**
5. Click **Continue** → **Register**
6. **Download the `.p8` file immediately** (you can only download it once)
7. Note the **Key ID** (10 characters)
8. Your **Team ID** is the same as your App Store Connect team (`iosTeamId` in `libs.versions.toml`)

## Files

| File | Content |
|------|---------|
| `secrets/apple/apn/APNAuthKey.p8` | The downloaded `.p8` file |
| `secrets/apple/apn/key_id` | APN Key ID (10 characters) |
| `secrets/apple/apn/team_id` | Apple Developer Team ID |

`config.rb` reads these files directly — no env bundle needed.

## Commands

```bash
mkdir -p secrets/apple/apn
cp ~/Downloads/AuthKey_*.p8 secrets/apple/apn/APNAuthKey.p8
echo "YOUR_APN_KEY_ID" > secrets/apple/apn/key_id
echo "YOUR_TEAM_ID"    > secrets/apple/apn/team_id
```

## Notes

- One APN key works for all your apps under the same Apple Developer account
- Keys don't expire but should be rotated periodically
- If you lose the `.p8` file you must revoke and regenerate
