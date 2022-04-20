package com.httprunnerjava.Common.Component;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Export {
    private List<String> content = new ArrayList<>();

    public Export(String export_var_name_strlist){
        content = JSONArray.parseArray(export_var_name_strlist,String.class);
    }

    public void update(Export export){
        this.content.addAll(export.getContent());
    }
}
