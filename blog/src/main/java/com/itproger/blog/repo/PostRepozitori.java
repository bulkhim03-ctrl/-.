package com.itproger.blog.repo;

import org.springframework.data.repository.CrudRepository;

import com.itproger.blog.models.Post;
//CrudRepository нужен обязательно, без него не работает БД, <Post это выбрал в model, а
// long тип данных для уникального индификатора
public interface PostRepozitori extends CrudRepository<Post, Long>{

}


