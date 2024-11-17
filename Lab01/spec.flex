// import sekcija

%%

// sekcija opcija i deklaracija
%class MPLexer
%function next_token
%line
%column
%debug
%type Yytoken

%eofval{
return new Yytoken( sym.EOF, null, yyline, yycolumn);
%eofval}

%{
//dodatni clanovi generisane klase
KWTable kwTable = new KWTable();
Yytoken getKW()
{
	return new Yytoken( kwTable.find( yytext() ),
	yytext(), yyline, yycolumn );
}
%}

//stanja
%state KOMENTAR
//makroi
slovo = [a-zA-Z]
cifra = [0-9]

%%

"//" { yybegin(KOMENTAR); } // Prelaz u stanje KOMENTAR
<KOMENTAR>\n { yybegin(YYINITIAL); } // Kraj komentara na novom redu
<KOMENTAR>. { ; } 

// pravila


[\t\n\r ] { ; }
\[ { return new Yytoken( sym.SQUARELEFTPAR, yytext(), yyline, yycolumn ); }
\] { return new Yytoken( sym.SQUARERIGHTPAR, yytext(), yyline, yycolumn ); }

//operatori
\+ { return new Yytoken( sym.PLUS,yytext(), yyline, yycolumn ); }
\- { return new Yytoken( sym.MINUS,yytext(), yyline, yycolumn ); }
//separatori
; { return new Yytoken( sym.SEMICOLON, yytext(), yyline, yycolumn ); }
:= { return new Yytoken( sym.ASSIGN, yytext(), yyline, yycolumn ); }

//kljucne reci
{slovo}+ { return getKW(); }
//identifikatori
{slovo}({slovo}|{cifra})* { return new Yytoken(sym.ID, yytext(),yyline, yycolumn ); }

//konstante
//bool
true|false { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn ); }
//int
0[0-7]+|0x[0-9a-fA-F]+|{cifra}+ { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn ); }
//float
0\.{cifra}+?(E(\+|-)?{cifra}+)? { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn ); }


//obrada gresaka
. { if (yytext() != null && yytext().length() > 0) System.out.println( "ERROR: " + yytext() ); }
