| Number       | Production Name              | Production |
|--------------|------------------------------|------------|
|  1.1         | *program* →                  | **program** **id** **(** *identifier_list* **)** **;** *program'* |
|  1.2.1       | *program'* →                 | *declarations* *program''* |
|  1.2.2       | *program'* →                 | *subprogram_declarations* *compound_statement* **.** |
|  1.2.3       | *program'* →                 | *compound_statement* **.** |
|  1.3.1       | *program''* →                | *subprogram_declarations* *compound_statement* **.** |
|  1.3.2       | *program''* →                | *compound_statement* **.** |
|  2.1.1       | *identifier_list* →          | **id** *identifier_list_tail* |
|  2.2.1       | *identifier_list_tail* →     | **,** **id** *identifier_list_tail* |
|  2.2.2       | *identifier_list_tail* →     | **ϵ**
|  3.1         | *declarations* →             | **var** **id** **:** type **;** *declarations'* |
|  3.2.1       | *declarations'* →            | *declarations* |
|  3.2.2       | *declarations'* →            | **ϵ** |
|  4.1         | *type* →                     | *standard_type* |
|  4.2         | *type* →                     | **array** **[** **num** **\.\.** **num** **]** **of** *standard_type* |
|  5.1         | *standard_type* →            | **integer** |
|  5.2         | *standard_type* →            | **real** |
|  6.1         | *subprogram_declarations* →  | *subprogram_declaration* **;** *subprogram_declarations'* |
|  6.2.1       | *subprogram_declarations'* → | *subprogram_declarations* |
|  6.2.2       | *subprogram_declarations'* → | **ϵ** |
|  7.1         | *subprogram_declaration* →   | *subprogram_head* *subprogram_declaration'* |
|  7.2.1       | *subprogram_declaration'* →  | *declarations* *subprogram_declaration''* |
|  7.2.2       | *subprogram_declaration'* →  | *subprogram_declarations* *compound_statement* |
|  7.2.3       | *subprogram_declaration'* →  | *compound_statement* |
|  7.3.1       | *subprogram_declaration''* → | *subprogram_declarations* *compound_statement* |
|  7.3.2       | *subprogram_declaration''* → | *compound_statement* |
|  8.1         | *subprogram_head* →          | **procedure** **id** *subprogram_head'* |
|  8.2.1       | *subprogram_head'* →         | *arguments* **;** |
|  8.2.2       | *subprogram_head'* →         | **;** |
|  9           | *arguments* →                | **(** *parameter_list* **)** |
| 10.1.1       | *parameter_list* →           | **id** **:** *type* *parameter_list_tail* |
| 10.2.1       | *parameter_list_tail* →      | **;** **id** **:** *type* *parameter_list_tail* |
| 10.2.2       | *parameter_list_tail* →      | **ϵ** |
| 11.1         | *compound_statement* →       | **begin** *compound_statment_tail* |
| 11.2.1       | *compound_statment_tail* →   | *statement_list* **end** |
| 11.2.2       | *compound_statment_tail* →   | **end** |
| 13.1.1       | *statement_list* →           | *statement* *statement_list_tail* |
| 13.2.1       | *statement_list_tail* →      | **;** *statement* *statement_list_tail* |
| 13.2.2       | *statement_list_tail* →      | **ϵ** |
| 14.1.1       | *statement* →                | *variable* **assignop** *expression* |
| 14.1.2       | *statement* →                | *procedure_statement* |
| 14.1.3       | *statement* →                | *compound_statement* |
| 14.1.4       | *statement* →                | **if** *expression* **then** *statement* *optional_else* |
| 14.1.5       | *statement* →                | **while** *expression* **do** *statement* |
| 14.2.1       | *optional_else* →            | **else** *statement* |
| 14.2.2       | *optional_else* →            | **ϵ** |
| 15.1         | *variable* →                 | **id** *array_variable* |
| 15.2.1       | *array_variable* →           | **[** *expression* **]** |
| 15.2.2       | *array_variable* →           | **ϵ** |
| 16.1         | *procedure_statement* →      | **call** **id** *optional_expression_list* |
| 16.2.1       | *optional_expression_list* → | **(** *expression_list* **)** |
| 16.2.2       | *optional_expression_list* → | **ϵ** |
| 17.1.1       | *expression_list* →          | *expression* *expression_list_tail* |
| 17.2.1       | *expression_list_tail* →     | **,** *expression* *expression_list_tail* |
| 17.2.2       | *expression_list_tail* →     | **ϵ** |
| 18.1         | *expression* →               | *simple_expression* *optional_relop* |
| 18.2.1       | *optional_relop* →           | **relop** *simple_expression* |
| 18.2.2       | *optional_relop* →           | **ϵ** |
| 19.1.1       | *simple_expression* →        | *term* *optional_addop* |
| 19.2.1       | *simple_expression* →        | *sign* *term* *optional_addop* |
| 19.3.1       | *optional_addop* →           | **addop** *term* *optional_addop* |
| 19.3.2       | *optional_addop* →           | **ϵ** |
| 20.1.1       | *term* →                     | *factor* *optional_mulop* |
| 20.2.1       | *optional_mulop* →           | **mulop** *factor* *optional_mulop* |
| 20.2.2       | *optional_mulop* →           | **ϵ** |
| 21.1.1       | *factor* →                   | **id** *array_expression* |
| 21.1.2       | *factor* →                   | **num** |
| 21.1.3       | *factor* →                   | **(** *expression* **)** |
| 21.1.4       | *factor* →                   | **not** *factor* |
| 21.2.1       | *array_expression* →         | **[** *expression* **]** |
| 21.2.2       | *array_expression* →         | **ϵ** |
| 22.1         | *sign* →                     | **+** |
| 22.2         | *sign* →                     | **-** |
