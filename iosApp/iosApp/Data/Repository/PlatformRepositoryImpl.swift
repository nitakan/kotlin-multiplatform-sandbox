//
//  ClientRepositoryImpl.swift
//  iosApp
//
//  Created by Masanori Kato on 2025/01/22.
//  Copyright © 2025 orgName. All rights reserved.
//

import Shared

class PlatformRepositoryImpl {}

extension PlatformRepositoryImpl: PlatformRepository {
    func getDataAsync(completionHandler: @escaping (PlatformData?, (any Error)?) -> Void) {
        DispatchQueue.global().asyncAfter(deadline: .now() + 1.0) {
            // エラーがなければデータを返す
            let data = PlatformData(value: "[iOS]The client generated async data")
            completionHandler(data, nil)
        }
    }
    
//    func getDataAsync() async throws -> PlatformData {
//        _ = try await Task.sleep(nanoseconds: 1000_000_000)
//        return PlatformData(value: "[iOS]The client generated async data")
//    }
    
    func getDataSync() -> PlatformData {
        return PlatformData(value: "[iOS]The client generated sync data")
    }
}
