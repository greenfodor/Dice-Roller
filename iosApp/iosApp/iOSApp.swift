import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        AppModulesKt.doInitKoin(config: nil)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
