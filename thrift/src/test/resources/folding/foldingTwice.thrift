service SomeService <fold text='{...}'>{
    void someFunc1(1: string req) throws <fold text='(...)'>(
        1: i64 code
    )</fold> (very_important)
    void someFunc2(1: string req) <fold text='(...)'>(
        not_important,
    )</fold>
    void someFunc3(1: string req) <fold text='(...)'>(very_lonnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnng_anno)</fold>
    void someFunc4<fold text='(...)'>(1: string very_lonnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnng_req)</fold>
    void someFunc5<fold text='(...)'>(
        1: string req1,
        2: string req2,
    )</fold>
}</fold>

struct SomeStruct1 <fold text='{...}'>{
    1: i32 someField1
    3: i32 someField2
    5: i32 someField3 xsd_optional xsd_nillable xsd_attrs<fold text='{...}'>{1: string someAttr}</fold>
}</fold>

const list<SomeStruct1> requestList = <fold text='[...]'>[<fold text='{...}'>{"someField1": "value1"}</fold>, <fold text='{...}'>{"someField1": "value2"}</fold>]</fold>;
