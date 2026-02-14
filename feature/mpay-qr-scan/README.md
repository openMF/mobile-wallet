# :feature:mpay-qr-scan module

This module handles QR code scanning for MPay transactions across all platforms (Android, iOS, Desktop, Web).

## Platform Setup

### iOS

Add the following keys to the `Info.plist` in your Xcode project:

```xml
<key>NSCameraUsageDescription</key>
<string>$(PRODUCT_NAME) camera description.</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>$(PRODUCT_NAME) photos description.</string>
```

## Dependency graph
![Dependency graph](../../docs/images/graphs-kmp/dep_graph_feature_mpay_qr_scan.svg)
