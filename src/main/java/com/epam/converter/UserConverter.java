package com.epam.converter;

import com.epam.model.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements Converter<User, DBObject> {

    @Override
    public DBObject convert(final User user) {
        final DBObject dbObject = new BasicDBObject();
        dbObject.put("id", user.getId().intValue());
        dbObject.put("name", user.getName());
        dbObject.put("surname", user.getSurname());
        dbObject.put("date_of_birth", user.getDateOfBirth());
        dbObject.put("city", user.getCity());
        dbObject.removeField("_class");
        return dbObject;
    }

}