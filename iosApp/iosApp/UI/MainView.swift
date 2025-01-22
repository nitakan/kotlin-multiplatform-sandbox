import SwiftUI
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared
import Dependencies

struct MainView: View {
    
    @Dependency(\.platformRepositoryFactory)
    var repositoryFactory: PlatformRepositoryFactory
    var body: some View {
        DisplayView(platformRepository: repositoryFactory.createPlatformRepository())
    }
}

struct DisplayView: View {
    
    @State private var displayData: DisplayData = DisplayData.Loading()
    
    var viewModel: DisplayDataViewModel
    
    init(platformRepository: PlatformRepository) {
        viewModel = DisplayDataViewModel(
            kmpRepository: KmpRepositoryImpl(),
            platformRepository: platformRepository
        )
    }
    
    var body: some View {
        VStack(alignment: .center, spacing: 4) {
            switch (displayData) {
            case is DisplayData.Loading:
                Text("Loading...")
            case let item as DisplayData.Loaded:
                Text("Loaded.")
                Text("ClientValue: " + item.clientValue.value)
                Text("KmpValue: " + item.kmpValue.value)
            default:
                Text("Failed")
            }
            
            Button("Load", action: {
                Task(
                    operation: {
                        try await viewModel.loadData()
                    }
                )
            })
            .padding(EdgeInsets.init(top: 16, leading: 0, bottom: 0, trailing: 0))
        }.task {
            do {
                for try await newValue in asyncSequence(for: viewModel.displayDataFlow) {
                    self.displayData = newValue
                }
            } catch {
                print("Error collecting flow: \(error)")
            }
        }
    }
}
