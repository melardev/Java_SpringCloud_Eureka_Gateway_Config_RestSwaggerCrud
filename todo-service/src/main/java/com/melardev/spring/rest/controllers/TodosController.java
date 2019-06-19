package com.melardev.spring.rest.controllers;

import com.melardev.spring.rest.dtos.responses.ErrorResponse;
import com.melardev.spring.rest.entities.Todo;
import com.melardev.spring.rest.repositories.TodosRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@Api(tags = {"v1", "Todos"})
@RestController
@RequestMapping("/todos")
public class TodosController {

    @Autowired
    private TodosRepository todosRepository;

    @ApiOperation(value = "Get all Todos", notes = "Get all todos, completed or not, as a JSON Array",
            produces = MediaType.APPLICATION_JSON_VALUE,
            protocols = "http", nickname = "getAll")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response",
                    response = Todo.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Server error")
    })
    @GetMapping("/")
    public Iterable<Todo> index() {
        return this.todosRepository.findAllHqlSummary();
    }

    @ApiOperation(value = "Get by Id", notes = "Get Todo JSON Object matching specified Id", produces = MediaType.APPLICATION_JSON_VALUE,
            protocols = "http", nickname = "getById")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = Todo.class),
            @ApiResponse(code = 404, message = "Not found response", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Server error response")
    })
    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable("id") Long id) {
        Optional<Todo> todo = this.todosRepository.findById(id);

        /*
                if (todo.isPresent())
            return new ResponseEntity<>(todo.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(new Todo(), HttpStatus.NOT_FOUND);
         */

        return todo.map(todo1 -> new ResponseEntity<>(todo1, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(new ErrorResponse("Not found"), HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Get pending Todos", notes = "Get pending todos, as a JSON Array", protocols = "http",
            produces = MediaType.APPLICATION_JSON_VALUE, nickname = "getNotCompletedTodos")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server error"),
            @ApiResponse(code = 200, message = "Successful response",
                    response = Todo.class, responseContainer = "List")})
    @GetMapping("/pending")
    public List<Todo> getNotCompletedTodos() {
        return this.todosRepository.findByHqlCompletedIs(false);
    }

    @ApiOperation(value = "Get completed Todos", notes = "Get completed todos, as a JSON Array", protocols = "http",
            produces = MediaType.APPLICATION_JSON_VALUE, nickname = "getCompletedTodos")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "id_param",
                    value = "todo id",
                    required = true,
                    paramType = "path",
                    dataTypeClass = String.class
            )
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server error"),
            @ApiResponse(code = 200, message = "Successful response",
                    response = Todo.class, responseContainer = "List")})
    @GetMapping("/completed")
    public List<Todo> getCompletedTodos() {
        return todosRepository.findByHqlCompletedIs(true);
    }


    @ApiOperation(value = "Create Todo", notes = "Tries to create a todo with the provided JSON payload", protocols = "http",
            produces = MediaType.APPLICATION_JSON_VALUE, httpMethod = "POST",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "body",
                    value = "body payload",
                    paramType = "body",
                    dataTypeClass = Todo.class
            ),
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Todo created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PostMapping("/")
    public ResponseEntity<Todo> create(@Valid @RequestBody Todo todo) {
        return new ResponseEntity<>(todosRepository.save(todo), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update Todo", notes = "Tries to update a todo with the provided JSON payload",
            protocols = "http",
            produces = MediaType.APPLICATION_JSON_VALUE, httpMethod = "PUT",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Todo updated", response = Todo.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PutMapping("/{id}")
    public ResponseEntity update(@ApiParam(value = "todoId",
            required = true, defaultValue = "1") @PathVariable("id") Long id,
                                 @RequestBody Todo todoInput) {
        Optional<Todo> optionalTodo = todosRepository.findById(id);
        if (optionalTodo.isPresent()) {
            Todo todo = optionalTodo.get();
            todo.setTitle(todoInput.getTitle());

            String description = todoInput.getDescription();
            if (description != null)
                todo.setDescription(description);

            todo.setCompleted(todoInput.isCompleted());
            return ResponseEntity.ok(todosRepository.save(optionalTodo.get()));
        } else {
            return new ResponseEntity<>(new ErrorResponse("This todo does not exist"), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete Todo", notes = "Tries to delete a todo with the provided JSON payload",
            protocols = "http",
            produces = MediaType.APPLICATION_JSON_VALUE, httpMethod = "DELETE")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Todo deleted", response = Void.class),
            @ApiResponse(code = 404, message = "Todo not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@ApiParam(value = "todoId",
            required = true, defaultValue = "1") @PathVariable("id") Long id) {
        Optional<Todo> todo = todosRepository.findById(id);
        if (todo.isPresent()) {
            todosRepository.delete(todo.get());
            return ResponseEntity.noContent().build();
        } else {
            return new ResponseEntity<>(new ErrorResponse("This todo does not exist"), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete All", notes = "Tries to delete ALL todos", protocols = "http",
            produces = MediaType.APPLICATION_JSON_VALUE, httpMethod = "DELETE", response = Void.class,
            code = 204)
    @DeleteMapping("/")
    public ResponseEntity deleteAll() {
        todosRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/after/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Todo> getByDateAfter(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
        Iterable<Todo> articlesIterable = todosRepository.findByCreatedAtAfter(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        List<Todo> articleList = new ArrayList<>();
        articlesIterable.forEach(articleList::add);
        return articleList;
    }

    @GetMapping(value = "/before/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Todo> getByDateBefore(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
        Iterable<Todo> articlesIterable = todosRepository.findByCreatedAtBefore(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        List<Todo> articleList = new ArrayList<>();
        articlesIterable.forEach(articleList::add);
        return articleList;
    }
}