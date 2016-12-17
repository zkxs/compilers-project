Production Name                       |**program**|**procedure**|**begin**|**call**|**id** |**num** |**var**|**integer**|**real**|**array**| **(** | **)** | **[** | **]** | **,** | **;** | **.** | **+** | **-** |**relop**|**addop**|**mulop**|**assignop**|**not** |**if** |**then**|**else** |**do** |**while**|**end** | **$** | 
--------------------------------------|-----------|-------------|---------|--------|-------|--------|-------|-----------|--------|---------|-------|-------|-------|-------|-------|-------|-------|-------|-------|---------|---------|---------|------------|--------|-------|--------|---------|-------|---------|--------|-------| 
*program* →                           | **program** **id** **(** *identifier_list* **)** **;** *program'* | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*program'* →                          | | *subprogram_declarations* *compound_statement* **.** | *compound_statement* **.** | | | | *declarations* *program''* | | | | | | | | | | | | | | | | | | | | | | | | | 
*program''* →                         | | *subprogram_declarations* *compound_statement* **.** | *compound_statement* **.** | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*identifier_list* →                   | | | | | **id** *identifier_list_tail* | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*identifier_list_tail* →              | | | | | | | | | | | | **ϵ** | | | **,** **id** *identifier_list_tail* | | | | | | | | | | | | | | | | | 
*declarations* →                      | | | | | | | **var** **id** **:** type **;** *optional_declarations* | | | | | | | | | | | | | | | | | | | | | | | | | 
*optional_declarations* →             | | **ϵ** | **ϵ** | | | | *declarations* | | | | | | | | | | | | | | | | | | | | | | | | | 
*type* →                              | | | | | | | | *standard_type* | *standard_type* | **array** **[** **num** **\.\.** **num** **]** **of** *standard_type* | | | | | | | | | | | | | | | | | | | | | | 
*standard_type* →                     | | | | | | | | **integer** | **real** | | | | | | | | | | | | | | | | | | | | | | | 
*subprogram_declarations* →           | | *subprogram_declaration* **;** *optional_subprogram_declarations* | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*optional_subprogram_declarations* →  | | *subprogram_declarations* | **ϵ** | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*subprogram_declaration* →            | | *subprogram_head* *subprogram_declaration'* | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*subprogram_declaration'* →           | | *subprogram_declarations* *compound_statement* | *compound_statement* | | | | *declarations* *subprogram_declaration''* | | | | | | | | | | | | | | | | | | | | | | | | | 
*subprogram_declaration''* →          | | *subprogram_declarations* *compound_statement* | *compound_statement* | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*subprogram_head* →                   | | **procedure** **id** *subprogram_head'* | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*subprogram_head'* →                  | | | | | | | | | | | *arguments* **;** | | | | | **;** | | | | | | | | | | | | | | | | 
*arguments* →                         | | | | | | | | | | | **(** *parameter_list* **)** | | | | | | | | | | | | | | | | | | | | | 
*parameter_list* →                    | | | | | **id** **:** *type* *parameter_list_tail* | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*parameter_list_tail* →               | | | | | | | | | | | | **ϵ** | | | | **;** **id** **:** *type* *parameter_list_tail* | | | | | | | | | | | | | | | | 
*compound_statement* →                | | | **begin** *compound_statment_tail* | | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*compound_statment_tail* →            | | | *statement_list* **end** | *statement_list* **end** | *statement_list* **end** | | | | | | | | | | | | | | | | | | | | *statement_list* **end** | | | | *statement_list* **end** | **end** | | 
*statement_list* →                    | | | *statement* *statement_list_tail* | *statement* *statement_list_tail* | *statement* *statement_list_tail* | | | | | | | | | | | | | | | | | | | | *statement* *statement_list_tail* | | | | *statement* *statement_list_tail* | | | 
*statement_list_tail* →               | | | | | | | | | | | | | | | | **;** *statement* *statement_list_tail* | | | | | | | | | | | | | | **ϵ** | | 
*statement* →                         | | | *compound_statement* | *procedure_statement* | *variable* **assignop** *expression* | | | | | | | | | | | | | | | | | | | | **if** *expression* **then** *statement* *optional_else* | | | | **while** *expression* **do** *statement* | | | 
*optional_else* →                     | | | | | | | | | | | | | | | | **ϵ** | | | | | | | | | | |  **else** *statement* ∪ **ϵ** | | **ϵ** | | | 
*variable* →                          | | | | | **id** *array_variable* | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*array_variable* →                    | | | | | | | | | | | | | **[** *expression* **]** | | | | | | | | | | **ϵ** | | | | | | | | | 
*procedure_statement* →               | | | | **call** **id** *optional_expression_list* | | | | | | | | | | | | | | | | | | | | | | | | | | | | 
*optional_expression_list* →          | | | | | | | | | | | **(** *expression_list* **)** | | | | | **ϵ** | | | | | | | | | | | **ϵ** | | | **ϵ** | | 
*expression_list* →                   | | | | | *expression* *expression_list_tail* | *expression* *expression_list_tail* | | | | | *expression* *expression_list_tail* | | | | | | | *expression* *expression_list_tail* | *expression* *expression_list_tail* | | | | | *expression* *expression_list_tail* | | | | | | | | 
*expression_list_tail* →              | | | | | | | | | | | | **ϵ** | | | **,** *expression* *expression_list_tail* | | | | | | | | | | | | | | | | | 
*expression* →                        | | | | | *simple_expression* *optional_relop* | *simple_expression* *optional_relop* | | | | | *simple_expression* *optional_relop* | | | | *simple_expression* *optional_relop* | | | *simple_expression* *optional_relop* | *simple_expression* *optional_relop* | | | | | *simple_expression* *optional_relop* | | | | | | | | 
*optional_relop* →                    | | | | | | | | | | | | **ϵ** | |**ϵ** | **ϵ** | **ϵ** | | | | **relop** *simple_expression* | | | | | | **ϵ** | **ϵ** | **ϵ** | | **ϵ** | | 
*simple_expression* →                 | | | | | *term* *optional_addop* | *term* *optional_addop* | | | | | *term* *optional_addop* | *sign* *term* *optional_addop* | | *sign* *term* *optional_addop* | *sign* *term* *optional_addop* | *sign* *term* *optional_addop* | | | | *sign* *term* *optional_addop* | | | | *term* *optional_addop* | | *sign* *term* *optional_addop* | *sign* *term* *optional_addop* | *sign* *term* *optional_addop* | | *sign* *term* *optional_addop* | | 
*optional_addop* →                    | | | | | | | | | | | | **ϵ** | |**ϵ** | **ϵ** | **ϵ** | | | | **ϵ** | **addop** *term* *optional_addop* | | | | | **ϵ** | **ϵ** |**ϵ** | | **ϵ** | | 
*term* →                              | | | | | *factor* *optional_mulop* | *factor* *optional_mulop* | | | | | *factor* *optional_mulop* | | | | | | | | | | | | | *factor* *optional_mulop* | | | | | | | | 
*optional_mulop* →                    | | | | | | | | | | | | **ϵ** | |**ϵ** | **ϵ** | **ϵ** | | | | **ϵ** | **ϵ** | **mulop** *factor* *optional_mulop* | | | | **ϵ** | **ϵ** |**ϵ** | | **ϵ** | | 
*factor* →                            | | | | | **id** *array_expression* | **num** | | | | | **(** *expression* **)** | | | | | | | | | | | | | **not** *factor* | | | | | | | | 
*array_expression* →                  | | | | | | | | | | | | **ϵ** | **[** *expression* **]** |**ϵ** | **ϵ** | **ϵ** | | | | **ϵ** | **ϵ** | **ϵ** | | | | **ϵ** | **ϵ** |**ϵ** | | **ϵ** | | 
*sign* →                              | | | | | | | | | | | | | | | | | | **+** | **-** | | | | | | | | | | | | | 
 