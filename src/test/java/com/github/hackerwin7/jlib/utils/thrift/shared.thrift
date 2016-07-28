namespace cpp shared
namespace d share
namespace dart shared
namespace java com.github.hackerwin7.jlib.utils.test.thrift.shared
namespace perl shared
namespace php shared
namespace haxe shared

struct SharedStruct {
    1: i32 key
    2: string value
}

service SharedService {
    SharedStruct getStruct(1: i32 key)
}