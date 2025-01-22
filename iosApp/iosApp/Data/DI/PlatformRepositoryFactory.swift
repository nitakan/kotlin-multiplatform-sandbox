import Shared

class PlatformRepositoryFactoryImpl {}

extension PlatformRepositoryFactoryImpl : PlatformRepositoryFactory {
    func createPlatformRepository() -> any PlatformRepository {
        return PlatformRepositoryImpl()
    }
}
