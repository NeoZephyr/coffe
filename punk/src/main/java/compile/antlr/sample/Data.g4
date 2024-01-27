grammar Data;

file : group+ ;

// $INT 表示之前匹配的整数规则（INT）的语法节点
// 通过 .int，访问整数语法节点的整数值
group: INT sequence[$INT.int] ;

// 语义动作（Semantic Action），它在语法规则解析过程中执行一些自定义逻辑
sequence[int n]
locals [int i = 1;]
: ( {$i<=$n}? INT {$i++;} )*
;
INT : [0-9]+ ;
WS : [ \t\n\r]+ -> skip ;