| Number | Production Name             | Production |
|--------|-----------------------------|------------|
|  1.1.1 | *program* →                 | **program** **id** **(** *identifier_list* **)** **;** *declarations* *subprogram_declarations* *compound_statement* **.** |
|  1.1.2 | *program* →                 | **program** **id** **(** *identifier_list* **)** **;** *declarations* *compound_statement* **.** |
|  1.2.1 | *program* →                 | **program** **id** **(** *identifier_list* **)** **;** *subprogram_declarations* *compound_statement* **.** |
|  1.2.2 | *program* →                 | **program** **id** **(** *identifier_list* **)** **;** *compound_statement* **.** |
|  2.1   | *identifier_list* →         | **id** |
|  2.2   | *identifier_list* →         | *identifier_list* **,** **id** |
|  3.1.1 | *declarations* →            | *declarations* **var** **id** **:** *type* **;** |
|  3.1.2 | *declarations* →            | **var** **id** **:** type **;** |
|  4.1   | *type* →                    | *standard_type* |
|  4.2   | *type* →                    | **array** **[** **num** **\.\.** **num** **]** **of** *standard_type* |
|  5.1   | *standard_type* →           | **integer** |
|  5.2   | *standard_type* →           | **real** |
|  6.1.1 | *subprogram_declarations* → | *subprogram_declarations* *subprogram_declaration* **;** |
|  6.1.2 | *subprogram_declarations* → | *subprogram_declaration* **;** |
|  7.1.1 | *subprogram_declaration* →  | *subprogram_head* *declarations* *subprogram_declarations* *compound_statement* |
|  7.1.2 | *subprogram_declaration* →  | *subprogram_head* *declarations* *compound_statement* |
|  7.2.1 | *subprogram_declaration* →  | *subprogram_head* *subprogram_declarations* *compound_statement* |
|  7.2.2 | *subprogram_declaration* →  | *subprogram_head* *compound_statement* |
|  8.1   | *subprogram_head* →         | **procedure** **id** *arguments* **;** |
|  8.2   | *subprogram_head* →         | **procedure** **id** **;** |
|  9.1   | *arguments* →               | **(** *parameter_list* **)** |
| 10.1   | *parameter_list* →          | **id** **:** *type* |
| 10.2   | *parameter_list* →          | *parameter_list* **;** **id** **:** *type* |
| 11.1   | *compound_statement* →      | **begin** *optional_statements* **end** |
| 11.2   | *compound_statement* →      | **begin** **end** |
| 12.1   | *optional_statements* →     | *statement_list* |
| 13.1   | *statement_list* →          | *statement* |
| 13.2   | *statement_list* →          | *statement_list* **;** *statement* |
| 14.1   | *statement* →               | *variable* **assignop** *expression* |
| 14.2   | *statement* →               | *procedure_statement* |
| 14.3   | *statement* →               | *compound_statement* |
| 14.4   | *statement* →               | **if** *expression* **then** *statement* **else** *statement* |
| 14.5   | *statement* →               | **if** *expression* **then** *statement* |
| 14.6   | *statement* →               | **while** *expression* **do** *statement* |
| 15.1   | *variable* →                | **id** |
| 15.2   | *variable* →                | **id** **[** *expression* **]** |
| 16.1   | *procedure_statement* →     | **call** **id** |
| 16.2   | *procedure_statement* →     | **call** **id** **(** *expression_list* **)** |
| 17.1   | *expression_list* →         | *expression* |
| 17.2   | *expression_list* →         | *expression_list* **,** *expression* |
| 18.1   | *expression* →              | *simple_expression* |
| 18.2   | *expression* →              | *simple_expression* **relop** *simple_expression* |
| 19.1   | *simple_expression* →       | *term* |
| 19.2   | *simple_expression* →       | *sign* *term* |
| 19.3   | *simple_expression* →       | *simple_expression* **addop** *term* |
| 20.1   | *term* →                    | *factor* |
| 20.2   | *term* →                    | *term* **mulop** *factor* |
| 21.1   | *factor* →                  | **id** |
| 21.2   | *factor* →                  | **id** **[** *expression* **]** |
| 21.3   | *factor* →                  | **num** |
| 21.4   | *factor* →                  | **(** *expression* **)** |
| 21.5   | *factor* →                  | **not** *factor* |
| 22.1   | *sign* →                    | **+** |
| 22.2   | *sign* →                    | **-** |
