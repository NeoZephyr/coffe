grammar Rows;

// 定义成员变量来存储解析过程中的状态信息
// 或者定义方法来执行特定的语义动作
// 这些成员和代码可以在语法规则中被调用和引用
@parser::members {
    private int col;

    public RowsParser(TokenStream input, int col) {
        this(input);
        this.col = col;
    }
}

file: (row NEWLINE)+ ;

row
locals [int i=0]
: ( STUFF
{
$i++;
if ( $i == col ) System.out.println($STUFF.text);
}
)+
;

TAB: '\t' -> skip ;
NEWLINE: '\r'? '\n' ;
STUFF: ~[\t\r\n]+ ;