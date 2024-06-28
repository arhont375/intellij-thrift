service SomeService <fold text='{...}'>{
    SomeResponse Do(1: SomeRequest request)
}</fold>

struct SomeRequest <fold text='{...}'>{
    1: string field
}</fold>

struct SomeResponse <fold text='{...}'>{
    1: string field
}</fold>

enum SomeEnum <fold text='{...}'>{
    ONE = 1,
    TWO = 2,
}</fold>

senum SomeSenum <fold text='{...}'>{
    "enum1",
    "enum2",
}</fold>

union SomeUnion1 <fold text='{...}'>{
    1: i32 someField1
    3: i32 someField2
    5: i32 someField3
}</fold>

exception SomeException <fold text='{...}'>{
    1: i32 someField1
    2: i32 someField2
    3: i32 someField3
}</fold>

const SomeRequest request = <fold text='{...}'>{"filed": "value" }</fold>;

<fold text='/*...*/'>/*
some comments
 */</fold>
