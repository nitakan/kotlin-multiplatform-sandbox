import Dependencies
import Shared

private enum PlatformRepositoryFactoryKey : DependencyKey {
    static let liveValue: any PlatformRepositoryFactory = PlatformRepositoryFactoryImpl()
}

extension DependencyValues {
    public var platformRepositoryFactory: any PlatformRepositoryFactory {
        get { self[PlatformRepositoryFactoryKey.self] }
        set { self[PlatformRepositoryFactoryKey.self] = newValue }
    }
}
