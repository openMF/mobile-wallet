//
//  ContentView.swift
//  iosApp
//
//  Created by Apple on 14/08/24.
//

import SwiftUI
import shared

let biometricUtil = BiometricUtilIosImpl()
struct ContentView: View {
    let greet = Greeting().greet()
    @State private var path = NavigationPath()

    var body: some View {
        ZStack {
            ComposeViewController()
        }
    }
}

struct ComposeViewController: UIViewControllerRepresentable {
    @StateObject var biometricAuthorizationViewModel: BiometricAuthorizationViewModel = BiometricAuthorizationViewModel()
    func makeUIViewController(context: Context) -> UIViewController {
        return App_iosKt.MainViewController(bioMetricUtil: biometricUtil, biometricViewModel: biometricAuthorizationViewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

extension BiometricAuthorizationViewModel: ObservableObject {}