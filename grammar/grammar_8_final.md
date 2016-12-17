| Number | Production Name                      | Production                                                            | Firsts                                           | Follows |
|--------|--------------------------------------|-----------------------------------------------------------------------|--------------------------------------------------|---------|
|  1.1   | *program* →                          | **program** **id** **(** *identifier_list* **)** **;** *program'*     | {**program**}                                    | {**$**}
|  1.2.1 | *program'* →                         | *declarations* *program'*                                             | {**var**}                                        | ↴
|  1.2.2 | *program'* →                         | *subprogram_declarations* *compound_statement* **.**                  | {**procedure**}                                  | ↴
|  1.2.3 | *program'* →                         | *compound_statement* **.**                                            | {**begin**}                                      | {**$**}
|  2.1.1 | *identifier_list* →                  | **id** *identifier_list_tail*                                         | {**id**}                                         | {**)**}
|  2.2.1 | *identifier_list_tail* →             | **,** **id** *identifier_list_tail*                                   | {**,**}                                          | ↴
|  2.2.2 | *identifier_list_tail* →             | **ϵ**                                                                 | {**ϵ**} →                                        | {**)**}
|  3.1   | *declarations* →                     | **var** **id** **:** *type* **;** *optional_declarations*               | {**var**}                                        | {**procedure**, **begin**}
|  3.2.1 | *optional_declarations* →            | *declarations*                                                        | {**var**}                                        | ↴
|  3.2.2 | *optional_declarations* →            | **ϵ**                                                                 | {**ϵ**} →                                        | {**procedure**, **begin**}
|  4.1   | *type* →                             | *standard_type*                                                       | {**integer**, **real**}                          | ↴
|  4.2   | *type* →                             | **array** **[** **num** **\.\.** **num** **]** **of** *standard_type* | {**array**}                                      | {**;**, **)**}
|  5.1   | *standard_type* →                    | **integer**                                                           | {**integer**}                                    | ↴
|  5.2   | *standard_type* →                    | **real**                                                              | {**real**}                                       | {**;**, **)**}
|  6.1   | *subprogram_declarations* →          | *subprogram_declaration* **;** *optional_subprogram_declarations*     | {**procedure**}                                  | {**begin**}
|  6.2.1 | *optional_subprogram_declarations* → | *subprogram_declarations*                                             | {**procedure**}                                  | ↴
|  6.2.2 | *optional_subprogram_declarations* → | **ϵ**                                                                 | {**ϵ**} →                                        | {**begin**}
|  7.1   | *subprogram_declaration* →           | *subprogram_head* *subprogram_declaration'*                           | {**procedure**}                                  | {**;**}
|  7.2.1 | *subprogram_declaration'* →          | *declarations* *subprogram_declaration'*                              | {**var**}                                        | ↴
|  7.2.2 | *subprogram_declaration'* →          | *subprogram_declarations* *compound_statement*                        | {**procedure**}                                  | ↴
|  7.2.3 | *subprogram_declaration'* →          | *compound_statement*                                                  | {**begin**}                                      | {**;**}
|  8.1   | *subprogram_head* →                  | **procedure** **id** *subprogram_head'*                               | {**procedure**}                                  | {**var**, **procedure**, **begin**}
|  8.2.1 | *subprogram_head'* →                 | *arguments* **;**                                                     | {**(**}                                          | ↴
|  8.2.2 | *subprogram_head'* →                 | **;**                                                                 | {**;**}                                          | {**var**, **procedure**, **begin**}
|  9     | *arguments* →                        | **(** *parameter_list* **)**                                          | {**(**}                                          | {**;**}
| 10.1.1 | *parameter_list* →                   | **id** **:** *type* *parameter_list_tail*                             | {**id**}                                         | {**)**}
| 10.2.1 | *parameter_list_tail* →              | **;** **id** **:** *type* *parameter_list_tail*                       | {**;**}                                          | ↴
| 10.2.2 | *parameter_list_tail* →              | **ϵ**                                                                 | {**ϵ**} →                                        | {**)**}
| 11.1   | *compound_statement* →               | **begin** *compound_statment_tail*                                    | {**begin**}                                      | {**.**, **;**, **id**, **call**, **begin**, **if**, **while**}
| 11.2.1 | *compound_statment_tail* →           | *statement_list* **end**                                              | {**id**, **call**, **begin**, **if**, **while**} | ↴
| 11.2.2 | *compound_statment_tail* →           | **end**                                                               | {**end**}                                        | {**.**, **;**, **id**, **call**, **begin**, **if**, **while**}
| 13.1.1 | *statement_list* →                   | *statement* *statement_list_tail*                                     | {**id**, **call**, **begin**, **if**, **while**} | {**end**}
| 13.2.1 | *statement_list_tail* →              | **;** *statement* *statement_list_tail*                               | {**;**}                                          | ↴
| 13.2.2 | *statement_list_tail* →              | **ϵ**                                                                 | {**ϵ**} →                                        | {**end**}
| 14.1.1 | *statement* →                        | *variable* **assignop** *expression*                                  | {**id**}                                         | ↴
| 14.1.2 | *statement* →                        | *procedure_statement*                                                 | {**call**}                                       | ↴
| 14.1.3 | *statement* →                        | *compound_statement*                                                  | {**begin**}                                      | ↴
| 14.1.4 | *statement* →                        | **if** *expression* **then** *statement* *optional_else*              | {**if**}                                         | ↴
| 14.1.5 | *statement* →                        | **while** *expression* **do** *statement*                             | {**while**}                                      | {**;**, **end**, **else**}
| 14.2.1 | *optional_else* →                    | **else** *statement*                                                  | {**else**}                                       | ↴
| 14.2.2 | *optional_else* →                    | **ϵ**                                                                 | {**ϵ**} →                                        | {**;**, **end**, **else**}
| 15.1   | *variable* →                         | **id** *array_variable*                                               | {**id**}                                         | {**assignop**}
| 15.2.1 | *array_variable* →                   | **[** *expression* **]**                                              | {**[**}                                          | ↴
| 15.2.2 | *array_variable* →                   | **ϵ**                                                                 | {**ϵ**} →                                        | {**assignop**}
| 16.1   | *procedure_statement* →              | **call** **id** *optional_expression_list*                            | {**call**}                                       | {**;**, **end**, **else**}
| 16.2.1 | *optional_expression_list* →         | **(** *expression_list* **)**                                         | {**(**}                                          | ↴
| 16.2.2 | *optional_expression_list* →         | **ϵ**                                                                 | {**ϵ**} →                                        | {**;**, **end**, **else**}
| 17.1.1 | *expression_list* →                  | *expression* *expression_list_tail*                                   | {**id**, **num**, **(**, **not**, **+**, **-**}  | {**)**}
| 17.2.1 | *expression_list_tail* →             | **,** *expression* *expression_list_tail*                             | {**,**}                                          | ↴
| 17.2.2 | *expression_list_tail* →             | **ϵ**                                                                 | {**ϵ**} →                                        | {**)**}
| 18.1   | *expression* →                       | *simple_expression* *optional_relop*                                  | {**id**, **num**, **(**, **not**, **+**, **-**}  | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**}
| 18.2.1 | *optional_relop* →                   | **relop** *simple_expression*                                         | {**relop**}                                      | ↴
| 18.2.2 | *optional_relop* →                   | **ϵ**                                                                 | {**ϵ**} →                                        | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**}
| 19.1.1 | *simple_expression* →                | *term* *optional_addop*                                               | {**id**, **num**, **(**, **not**}                | ↴
| 19.1.2 | *simple_expression* →                | *sign* *term* *optional_addop*                                        | {**+**, **-**}                                   | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**, **relop**}
| 19.2.1 | *optional_addop* →                   | **addop** *term* *optional_addop*                                     | {**addop**}                                      | ↴
| 19.2.2 | *optional_addop* →                   | **ϵ**                                                                 | {**ϵ**} →                                        | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**, **relop**}
| 20.1.1 | *term* →                             | *factor* *optional_mulop*                                             | {**id**, **num**, **(**, **not**}                | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**, **relop**, **addop**}
| 20.2.1 | *optional_mulop* →                   | **mulop** *factor* *optional_mulop*                                   | {**mulop**}                                      | ↴
| 20.2.2 | *optional_mulop* →                   | **ϵ**                                                                 | {**ϵ**} →                                        | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**, **relop**, **addop**}
| 21.1.1 | *factor* →                           | **id** *array_expression*                                             | {**id**}                                         | ↴
| 21.1.2 | *factor* →                           | **num**                                                               | {**num**}                                        | ↴
| 21.1.3 | *factor* →                           | **(** *expression* **)**                                              | {**(**}                                          | ↴
| 21.1.4 | *factor* →                           | **not** *factor*                                                      | {**not**}                                        | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**, **relop**, **addop**, **mulop**}
| 21.2.1 | *array_expression* →                 | **[** *expression* **]**                                              | {**[** }                                         | ↴
| 21.2.2 | *array_expression* →                 | **ϵ**                                                                 | {**ϵ**} →                                        | {**;**, **end**, **else**, **then**, **do**, **]**, **)**, **,**, **relop**, **addop**, **mulop**}
| 22.1   | *sign* →                             | **+**                                                                 | {**+**}                                          | ↴
| 22.2   | *sign* →                             | **-**                                                                 | {**-**}                                          | {**id**, **num**, **(**, **not**}
