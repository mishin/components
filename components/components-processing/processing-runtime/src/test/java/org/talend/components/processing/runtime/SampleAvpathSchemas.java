package org.talend.components.processing.runtime;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.IndexedRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.collection.JavaConversions;
import scala.collection.LinearSeq;
import scala.collection.immutable.List;
import scala.util.Try;
import wandou.avpath.Evaluator.Ctx;
import wandou.avpath.package$;

/**
 * Sample Avro data to test avpath.
 */
public class SampleAvpathSchemas {

    /**
     * Tools to create IndexedRecords that match the following pseudo-avro:
     * 
     * <pre>
     * {
     *   "automobiles" : [
     *     { "maker" : "Nissan", "model" : "Teana", "year" : 2011 },
     *     { "maker" : "Honda", "model" : "Jazz", "year" : 2010 },
     *     { "maker" : "Honda", "model" : "Civic", "year" : 2007 },
     *     { "maker" : "Toyota", "model" : "Yaris", "year" : 2008 },
     *     { "maker" :* "Honda", "model" : "Accord", "year" : 2011 }
     *   ],
     *   "motorcycles" : [{ "maker" : "Honda", "model" : "ST1300", "year" : 2012 }]
     * }
     * </pre>
     */

    public final static Schema RECORD_VEHICLE = SchemaBuilder.record("vehicle").fields() //
            .requiredString("maker") //
            .requiredString("model") //
            .requiredInt("year") //
            .endRecord();

    public final static Schema RECORD_VEHICLE_COLLECTION = SchemaBuilder.record("vehicleCollection").fields() //
            .name("automobiles").type().array().items().type(RECORD_VEHICLE).noDefault() //
            .name("motorcycles").type().array().items().type(RECORD_VEHICLE).noDefault() //
            .endRecord();

    public static final IndexedRecord createVehicleRecord(String maker, String model, int year) {
        IndexedRecord ir = new GenericData.Record(RECORD_VEHICLE);
        ir.put(0, maker);
        ir.put(1, model);
        ir.put(2, year);
        return ir;
    }

    public static final IndexedRecord createVehicleCollection(Collection<IndexedRecord> automobiles,
            Collection<IndexedRecord> motorcycles) {
        IndexedRecord r = new Record(RECORD_VEHICLE_COLLECTION);
        r.put(0, new GenericData.Array<IndexedRecord>(RECORD_VEHICLE_COLLECTION.getFields().get(0).schema(), automobiles));
        r.put(1, new GenericData.Array<IndexedRecord>(RECORD_VEHICLE_COLLECTION.getFields().get(1).schema(), motorcycles));
        return r;
    }

    public static final IndexedRecord getDefaultVehicleCollection() {
        return createVehicleCollection(Arrays.asList(createVehicleRecord("Nissan", "Teana", 2011), //
                createVehicleRecord("Honda", "Jazz", 2010), //
                createVehicleRecord("Honda", "Civic", 2007), //
                createVehicleRecord("Toyota", "Yaris", 2017), //
                createVehicleRecord("Toyota", "Yaris", 2016), //
                createVehicleRecord("Honda", "Accord", 2011)), //
                Arrays.asList(createVehicleRecord("Honda", "ST1300", 2012)));
    }

    /**
     * Tools to create IndexedRecords that match the following pseudo-avro:
     * 
     * <pre>
     * {
     *     "books" : [
     *     {
     *         "id"     : 1,
     *             "title"  : "Clean Code",
     *             "author" : { "name" : "Robert C. Martin" },
     *         "price"  : 17.96
     *     },
     *    {
     *        "id"     : 2,
     *            "title"  : "Maintainable JavaScript",
     *            "author" : { "name" : "Nicholas C. Zakas" },
     *        "price"  : 10
     *    },
     *    {
     *        "id"     : 3,
     *            "title"  : "Agile Software Development",
     *            "author" : { "name" : "Robert C. Martin" },
     *        "price"  : 20
     *    },
     *    {
     *        "id"     : 4,
     *            "title"  : "JavaScript: The Good Parts",
     *            "author" : { "name" : "Douglas Crockford" },
     *        "price"  : 15.67
     *    }
     *    ]
     *}
     * </pre>
     */

    public final static Schema RECORD_BOOK = SchemaBuilder.record("book").fields() //
            .requiredInt("id") //
            .requiredString("title") //
            .requiredString("author") //
            .requiredFloat("price") //
            .endRecord();

    public final static Schema RECORD_BOOK_COLLECTION = SchemaBuilder.record("books").fields() //
            .name("books").type().array().items().type(RECORD_BOOK).noDefault() //
            .endRecord();

    public static final IndexedRecord createBook(int id, String title, String author, float price) {
        IndexedRecord ir = new GenericData.Record(RECORD_BOOK);
        ir.put(0, id);
        ir.put(1, title);
        ir.put(2, author);
        ir.put(3, price);
        return ir;
    }

    public static final IndexedRecord createBookCollection(IndexedRecord... books) {
        IndexedRecord r = new Record(RECORD_BOOK_COLLECTION);
        r.put(0, new GenericData.Array<IndexedRecord>(RECORD_BOOK_COLLECTION.getFields().get(0).schema(), Arrays.asList(books)));
        return r;
    }

    public static final IndexedRecord getDefaultBooksCollection() {
        return createBookCollection(createBook(1, "Clean Code", "Robert C. Martin", 17.96f), //
                createBook(2, "Maintainable JavaScript", "Nicholas C. Zakas", 10.0f),
                createBook(3, "Agile Software Development", "Robert C. Martin", 20.0f),
                createBook(4, "JavaScript: The Good Parts", "Douglas Crockford", 15.67f));
    }
}
