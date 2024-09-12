import Foundation
import shared
import LocalAuthentication

class BiometricUtilIosImpl: BioMetricUtil {

    private let cipherUtil = CipherUtilIosImpl()
    
    private var promptDescription: String = "Authenticate"
    
    func setAndReturnPublicKey(completionHandler: @escaping (String?, (any Error)?) -> Void) {
     
        let laContext = LAContext()
        laContext.localizedReason = promptDescription
        laContext.localizedFallbackTitle = "Cancel"
        laContext.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, localizedReason: promptDescription) {
            [weak self] success, authenticationError in

            DispatchQueue.main.async {
                if success {
                    completionHandler(self?.generatePublicKey(), nil)
                } else {
                    completionHandler(nil, authenticationError)
                }
            }
        }
    }
    
    func authenticate() async throws -> AuthenticationResult {
        do {
            _ = try self.cipherUtil.getCrypto()
            return AuthenticationResult.Success()
        } catch {
            print("AuthenticateError: \(error.localizedDescription)")
            return AuthenticationResult.Error(error: error.localizedDescription)
        }
    }
    
    func canAuthenticate() -> Bool {
        var error: NSError?
        let laContext = LAContext()
        return laContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error)
    }
    
    
    func generatePublicKey() -> String? {
        let keyPair = try? cipherUtil.generateKeyPair()
        return keyPair?.publicKey?.toPemFormat().toBase64()
    }
    
    func getPublicKey() -> String? {
        return cipherUtil.getPublicKey()?.encoded?.toPemFormat().toBase64()
    }
    
    func isBiometricSet() -> Bool {
        return (getPublicKey() != nil) && isValidCrypto()
    }
    
    func isValidCrypto() -> Bool {
        do {
            _ = try cipherUtil.getCrypto()
            return true
        } catch {
            return false
        }
    }
    
    func signUserId(ucc: String) -> String {
        guard let data = ucc.data(using: .utf8) else {
            print("Failed to convert UCC to data")
            fatalError()
        }
        
        var error: Unmanaged<CFError>?
        guard let signature = SecKeyCreateSignature(cipherUtil.getKey()!, .rsaSignatureMessagePKCS1v15SHA256, data as CFData, &error) else {
            if let error = error {
                print("Error creating signature: \(error.takeRetainedValue())")
            }
            fatalError()
        }
        
        return (signature as Data).base64EncodedString()
    }
    
    
}

extension String {
    func toPemFormat() -> String {
        let chunkSize = 64
        var pemString = "-----BEGIN RSA PUBLIC KEY-----\n"
        var base64String = self
        while base64String.count > 0 {
            let chunkIndex = base64String.index(base64String.startIndex, offsetBy: min(chunkSize, base64String.count))
            let chunk = base64String[..<chunkIndex]
            pemString.append(contentsOf: chunk)
            pemString.append("\n")
            base64String = String(base64String[chunkIndex...])
        }
        pemString.append("-----END RSA PUBLIC KEY-----")
        return pemString
    }
}

extension String {
    func toBase64() -> String? {
        guard let data = self.data(using: .utf8) else {
            return nil
        }
        return data.base64EncodedString()
    }
}