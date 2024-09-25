struct <TYPO>Userr</TYPO> {
  1: required string <TYPO>nmae</TYPO>
}

enum <TYPO>Mothed</TYPO>
{
  GET = 1,
  POST = 2,
  <TYPO>UNKOW</TYPO> = 3,
}

exception <TYPO>Xceptoin</TYPO> {
  1: i32 <TYPO>erroor</TYPO>Code,
  2: string message
}

service Typo<TYPO>Srevice</TYPO> // 7
{
  string <TYPO>Naem</TYPO>(1: string <TYPO>tyypo</TYPO>), // <TYPO>commemt</TYPO>
}
