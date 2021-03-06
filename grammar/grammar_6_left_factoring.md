| Number       | Production Name              | Production |
|--------------|------------------------------|------------|
|  1.1         | *program* →                  | **program** **id** **(** *identifier_list* **)** **;** *program'* |
|  1.2.1       | *program'* →                 | *declarations* *program''* |
|  1.2.2       | *program'* →                 | *subprogram_declarations* *compound_statement* **.** |
|  1.2.3       | *program'* →                 | *compound_statement* **.** |
|  1.3.1       | *program''* →                | *subprogram_declarations* *compound_statement* **.** |
|  1.3.2       | *program''* →                | *compound_statement* **.** |
|  2.1.1       | *identifier_list* →          | **id** *identifier_list'* |
|  2.2.1       | *identifier_list'* →         | **,** **id** *identifier_list'* |
|  2.2.2       | *identifier_list'* →         | **ϵ** |
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
| 10.1.1       | *parameter_list* →           | **id** **:** *type* *parameter_list'* |
| 10.2.1       | *parameter_list'* →          | **;** **id** **:** *type* *parameter_list'* |
| 10.2.2       | *parameter_list'* →          | **ϵ** |
| 11.1         | *compound_statement* →       | **begin** *compound_statement'* |
| 11.2.1       | *compound_statement'* →      | *statement_list* **end** |
| 11.2.2       | *compound_statement'* →      | **end** |
| 13.1.1       | *statement_list* →           | *statement* *statement_list'* |
| 13.2.1       | *statement_list'* →          | **;** *statement* *statement_list'* |
| 13.2.2       | *statement_list'* →          | **ϵ** |
| 14.1.1       | *statement* →                | *variable* **assignop** *expression* |
| 14.1.2       | *statement* →                | *procedure_statement* |
| 14.1.3       | *statement* →                | *compound_statement* |
| 14.1.4       | *statement* →                | **if** *expression* **then** *statement* *statement'* |
| 14.1.5       | *statement* →                | **while** *expression* **do** *statement* |
| 14.2.1       | *statement'* →               | **else** *statement* |
| 14.2.2       | *statement'* →               | **ϵ** |
| 15.1         | *variable* →                 | **id** *variable'* |
| 15.2.1       | *variable'* →                | **[** *expression* **]** |
| 15.2.2       | *variable'* →                | **ϵ** |
| 16.1         | *procedure_statement* →      | **call** **id** *procedure_statement'* |
| 16.2.1       | *procedure_statement'* →     | **(** *expression_list* **)** |
| 16.2.2       | *procedure_statement'* →     | **ϵ** |
| 17.1.1       | *expression_list* →          | *expression* *expression_list'* |
| 17.2.1       | *expression_list'* →         | **,** *expression* *expression_list'* |
| 17.2.2       | *expression_list'* →         | **ϵ** |
| 18.1         | *expression* →               | *simple_expression* *expression'* |
| 18.2.1       | *expression'* →              | **relop** *simple_expression* |
| 18.2.2       | *expression'* →              | **ϵ** |
| 19.1.1       | *simple_expression* →        | *term* *simple_expression'* |
| 19.2.1       | *simple_expression* →        | *sign* *term* *simple_expression'* |
| 19.3.1       | *simple_expression'* →       | **addop** *term* *simple_expression'* |
| 19.3.2       | *simple_expression'* →       | **ϵ** |
| 20.1.1       | *term* →                     | *factor* *term'* |
| 20.2.1       | *term'* →                    | **mulop** *factor* *term'* |
| 20.2.2       | *term'* →                    | **ϵ** |
| 21.1.1       | *factor* →                   | **id** *factor'* |
| 21.1.2       | *factor* →                   | **num** |
| 21.1.3       | *factor* →                   | **(** *expression* **)** |
| 21.1.4       | *factor* →                   | **not** *factor* |
| 21.2.1       | *factor'* →                  | **[** *expression* **]** |
| 21.2.2       | *factor'* →                  | **ϵ** |
| 22.1         | *sign* →                     | **+** |
| 22.2         | *sign* →                     | **-** |
