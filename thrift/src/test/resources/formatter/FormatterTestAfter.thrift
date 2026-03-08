service SomeService1 {
    void someFunc1(1: string req)
    void someFunc2(1: string req)
}

struct SomeStruct1 {
    1: i32 someField1,
    3: i32 someField2 xsd_nillable xsd_attrs {
        1: i32 someAttr1
    } (someAnno = "val")
} (someAnno = "val", someAnno = "val", someAnno = "val", someAnno = "val", someAnno = "val", someAnno = "val", someAnno = "val", someAnno = "val", someAnno = "val")

exception SomeException {
    1: i32 code
}

exception AnnotatedException {
    1: i32 code
} (deprecated)

service SomeService2 {
    void shortArgs(1: i32 a, 2: i32 b, 3: i32 c, 4: i32 d) (deprecated)
    void longMethod1(1: string someVeryLongArgumentName1, 2: i64 anotherVeryLongArgumentName2, 3: string thirdVeryLongArgumentName3, 4: i64 fourthVeryLongArgumentName4)
    void longMethod2(1: string someVeryLongArgumentName1, 2: i64 anotherVeryLongArgumentName2, 3: string thirdVeryLongArgumentName3, 4: i64 fourthVeryLongArgumentName4, 5: string fifthVeryLongArgumentName5, 6: i64 sixthVeryLongArgumentName6) throws (1: SomeException sexc1, 25: SomeException sexc3 xsd_attrs {
        1: string someAttr
    }) (deprecated)
    void annotationWrap(1: string someVeryLongArgumentName1, 2: i64 anotherVeryLongArgumentName2, 3: string thirdVeryLongArg) (deprecated)
}

const list<string> i64List = ["some short text", "some long text", "some short text"];

struct SomeRequest {
    1: string field1
    2: string field2
}

const SomeRequest request = {
    "field1": "value",
    "field2": "value",
    "field3": "value"
};

enum SomeEnum {
    field1 = 1,
    field2 = 2,
    field3 = 3
}

struct WithCommentsAndSpace {
    // comments beg
    1: i64 a;

    /*
        between
     */

    // test
    2: i64 b;

    // comment2 end
}
