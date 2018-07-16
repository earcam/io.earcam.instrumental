grammar Manifest;



// TODO skip comments,  auto-join-up split-lines can we use `NL SPACE -> skip` to achieve this????

manifest
	: 'Manifest-Version' ': ' Extended NL
	( imports
	| exports
	| symbolicName
	| bundleManifestVersion
	| bundleActivator
	| fragmentHost
	| dynamicImports
	| genericManifestEntry
	)* NL
	( manifestAttributes)?
	NL*
	EOF
	;


bundleManifestVersion
	: 'Bundle-ManifestVersion' ': ' ( Number | AlphaNum | Digit ) NL;

bundleActivator                //FIXME remove Extended, only uniqueName
	: 'Bundle-Activator' ': ' ( UniqueName | Extended ) NL;
	

symbolicName
	: 'Bundle-SymbolicName' ': ' Extended (';' parameter)* NL;

fragmentHost
	: 'Fragment-Host' ': ' Extended (';' parameter)* NL;

manifestAttributes
	: (manifestAttribute)+
	;

manifestAttribute
	: (genericManifestEntry)+ NL
	;

dynamicImports
	: 'DynamicImport-Package' ': ' 
	( dynamicDescription
	| dynamicDescription (',' WS* dynamicDescription)+
	) NL
	;

dynamicDescription
	: wildcardName (';' WS* parameter)*?
	| wildcardName (';' WS* wildcardName)+? (';' parameter)*;

wildcardName
	: ( ( UniqueName | Extended )+? '*' )   // TODO this should end with '.*' but paquet is greedy
	| paquet
	| '*'
	;

imports
	: 'Import-Package: ' 
	( port
	| port (',' WS* port)+
	) NL
	;

exports
	: 'Export-Package: ' 
	( port
	| port (',' WS* port)+
	) NL
	;

port
	: paquet (';' WS* parameter)*?
	| paquet (';' WS* paquet)+? (';' parameter)*;



paquet                //FIXME remove Extended, only uniqueName
	: UniqueName
	| Extended;


parameter    : directive | attribute;
// parameter    : versionAttribute | directive | attribute;
// versionAttribute : 'version' '=' ( QuotedVersionRange | '"' Version '"' | Version ) ;
directive    : Extended ':=' argument;
attribute    : Extended '=' argument;

Alpha    : [a-zA-Z];
AlphaNum : Alpha | Digit;
Number : Digit+ ;
Digit  : [0-9] ;


/*
Version : Number ( '.' Number ( '.' Number ( '.' ( AlphaNum | '_' | '-' )+ )? )? )? ;
VersionRange : ( Interval | Version ) ;
Interval : ( '[' | '(' ) Version ',' Version ( ']' | ')' ) ;
QuotedVersionRange : '"' VersionRange '"' ;
*/

Path                                          // TODO spaces
	: '/'? Extended ( '/' Extended)+ '/'?;


Extended : ( AlphaNum | '_' | '-' | '.' )+ ;

//Digest
//	: AlphaNum+ '=';  //FIXME ffs


QuotedString : '"' ~[\u000D\u000A\u0000"\\]* '"';
argument: Extended | QuotedString | AlphaNum;


NL
	: ('\r\n' | '\n');

WS
    : [ \t\u000C]+;

UniqueName : Identifier 
	| Identifier ('.' Identifier )+;

Identifier
	:	JavaLetter JavaLetterOrDigit*
	;

fragment
JavaLetter
	:	[a-zA-Z$_] // these are the "java letters" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

fragment
JavaLetterOrDigit
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

ANYCHAR: ( . | '(' | ')' | '=' | '/' | ',' );
	
//genericManifestEntry: Extended ': ' ( Path | Extended | Number | AlphaNum | Digit ) NL;
//genericManifestEntry: Extended ': ' ( ' ' | '*' | '(' | ')' | ',' | '"' | '\'' | JavaLetterOrDigit | '&' | '@' | ~NL | Path | Extended | Number | AlphaNum | Digit ) NL;

anything
	: 
	( Alpha
	| AlphaNum
	| Digit
	| Number
	| Path
	| Extended
	| QuotedString
	| UniqueName
	| Identifier
	| WS
	| '*'
	| '@'
	| ','
	| ';'
	| ':'
	| '&'
	| '('
	| ')'
	| '!'
	| '~'
	| '+'
	| '$'
	| '='
	| ':='
	| ANYCHAR
	) +
	;

genericManifestEntry: Extended ': ' anything NL;
	