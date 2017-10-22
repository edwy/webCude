package com.webcube.model;

import com.blade.jdbc.annotation.Table;
import com.blade.jdbc.core.ActiveRecord;
import lombok.Data;

@Table(value = "book_info")
@Data
public class BookInfo extends ActiveRecord {

    private Integer id;
    private String  book_name;
    private String  book_summary;
    private String book_url;

}
