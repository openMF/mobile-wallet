import Foundation
import shared

class CipherUtilIosImpl: ICipherUtil {
    private let KEY_NAME = "my_biometric_key"
    private let tag: Data
    
    private lazy var key: SecKey? = {
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: tag,
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
            kSecReturnRef as String: true
        ]
        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        guard status == errSecSuccess else { return nil }
        return (item as! SecKey)
    }()
    
    init() {
        self.tag = KEY_NAME.data(using: .utf8)!
    }
    
    func generateKeyPair() throws -> CommonKeyPair {
        let access = SecAccessControlCreateWithFlags(
            nil,
            kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly,
            .userPresence,
            nil
        )!
        let attributes: [String: Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
            kSecAttrKeySizeInBits as String: 2048,
            kSecPrivateKeyAttrs as String: [
                kSecAttrIsPermanent as String: true,
                kSecAttrApplicationTag as String: tag,
                kSecAttrAccessControl as String: access,
            ]
        ]
        
        var error: Unmanaged<CFError>?
        guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error) else {
            throw error!.takeRetainedValue() as Error
        }
        guard let publicKey = SecKeyCopyPublicKey(privateKey) else {
            throw NSError(domain: NSOSStatusErrorDomain, code: Int(errSecInternalError), userInfo: nil)
        }
                
        let publicKeyData = SecKeyCopyExternalRepresentation(publicKey, nil)! as Data
        let privateKeyData = SecKeyCopyExternalRepresentation(privateKey, nil)! as Data
        
        return CommonKeyPair(publicKey: publicKeyData.base64EncodedString(), privateKey: privateKeyData.base64EncodedString())
    }
    
    func getCrypto() throws -> Crypto {
        guard let privateKey = getKey() else {
            throw NSError(domain: NSOSStatusErrorDomain, code: Int(errSecItemNotFound), userInfo: nil)
        }
        
        let publicKey = SecKeyCopyPublicKey(privateKey)!
        let publicKeyData = SecKeyCopyExternalRepresentation(publicKey, nil)! as Data
        let privateKeyData = SecKeyCopyExternalRepresentation(privateKey, nil)! as Data
        
        UserDefaults.standard.setValue(publicKeyData.base64EncodedString(), forKey: "PublicKey")
        
        return Crypto()
    }
    
    func getPublicKey() -> (any CommonPublicKey)? {
        let savedPublicKey = UserDefaults.standard.string(forKey: "PublicKey")
        if (savedPublicKey != nil) {
            return CommonPublicKeyImpl(encoded: savedPublicKey!)
        }
        
        guard let privateKey = getKey() else { return CommonPublicKeyImpl(encoded: "") }
        guard let publicKey = SecKeyCopyPublicKey(privateKey) else { return CommonPublicKeyImpl(encoded: "") }
        let publicKeyData = SecKeyCopyExternalRepresentation(publicKey, nil)! as Data
        let publicKeyString = publicKeyData.base64EncodedString()
        UserDefaults.standard.setValue(publicKeyString, forKey: "PublicKey")
        return CommonPublicKeyImpl(encoded: publicKeyString)
    }
    
    
    func removePublicKey() async throws {
        UserDefaults.standard.removeObject(forKey: "PublicKey")
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: tag,
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA
        ]
        let status = SecItemDelete(query as CFDictionary)
        if status != errSecSuccess && status != errSecItemNotFound {
            throw NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: nil)
        }
    }
    
    func getKey() -> SecKey? {
        return key
    }
    
}
