package com.kpi_service.kpi_service.app.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

public class MicrosoftDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        long millis = date.getTime();
        gen.writeString("/Date(" + millis + ")/");
    }
}

