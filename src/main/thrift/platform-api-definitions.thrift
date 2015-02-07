namespace * com.mashery.papi.thrift
struct Domain {
    1: required string address
}

struct Service {
    1: optional string name,
    2: optional i32 qpsLimitOverall
}

struct EndPoint {
    1: optional string name,
    2: optional set<Domain> publicDomains,
    3: optional bool inboundSslRequired,
    4: optional string trafficManagerDomain,
    5: optional string requestPathAlias,
    6: optional set<Domain> systemDomains,
    7: optional string outboundRequestTargetPath,
    8: optional string requestProtocol,
    9: optional set<string> apiMethodDetectionLocations,
    10: optional string requestAuthenticationType,
    11: optional set<string> apiKeyValueLocations,
    12: optional string apiKeyValueLocationKey
}