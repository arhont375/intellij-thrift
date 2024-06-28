struct SomeStruct1 <fold text='{...}'>{
    1: i32 someField1
    3: i32 someField2
    5: i32 someField3 xsd_optional xsd_nillable xsd_attrs<fold text='{...}'>{1: string someAttr}</fold>
}</fold>

const list<SomeStruct1> requestList = <fold text='[...]'>[<fold text='{...}'>{"someField1": "value1"}</fold>, <fold text='{...}'>{"someField1": "value2"}</fold>]</fold>;