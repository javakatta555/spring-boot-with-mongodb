package com.example.mongo.dao;

import com.example.mongo.config.MongoConfig;
import com.mongodb.client.result.DeleteResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;



import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.BulkOperations.BulkMode.UNORDERED;


@Slf4j
public abstract class AbstractDAO<T> {

    @Autowired
    MongoConfig mongoConfig;

    /**
     * Returns the name of the database to connect to.
     * For connecting your dao to Product db, override this method and return 'Product' as db name
     */


    protected final MongoOperations getMongoOperations(String database) {
        return determineMongoTemplate(database);
    }

    private MongoTemplate determineMongoTemplate(final String database) {
       return mongoConfig.getMongoTemplateFromMap(database);
    }

    public T findById(final String database,Class<T> clazz, Object id)  {
        final MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.findById(id, clazz);
    }

    public List<T> findAll(final String database,Class<T> clazz) {
        final MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.findAll(clazz);
    }

    public T findOneByProperty(final String database,Class<T> clazz, String key, String value)  {
        final Query query = new Query();
        query.addCriteria(where(key).is(value));
        final MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.findOne(query, clazz);
    }

    public List<T> findListByProperties(final String database,Class<T> clazz, Map<String, Object> properties)  {
        List<T> list;
        MongoOperations mongoOperations = getMongoOperations(database);

        Query query = new Query();
        if (!CollectionUtils.isEmpty(properties)) {
            Iterator itr = properties.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry pairs = (Map.Entry) itr.next();
                String key = (String) pairs.getKey();
                Object value = pairs.getValue();
                query.addCriteria(where(key).is(value));
            }
        }
        list = mongoOperations.find(query, clazz);
        return list;
    }

    public T findOneByProperties(final String database,Class<T> clazz, Map<String, Object> properties)  {
        List<T> byProperties = findListByProperties(database,clazz, properties);
        if (!CollectionUtils.isEmpty(byProperties)) {
            return byProperties.get(0);
        }
        return null;
    }

    public T findOneByPropertyWithProjection(final String database,Class<T> clazz, String key, Object value, List<String> projection) {
        MongoOperations mongoOperations = getMongoOperations(database);
        Query query = new Query();
        if (!CollectionUtils.isEmpty(projection)) {
            for (String projectionField : projection) {
                query.fields().include(projectionField);
            }
        }
        query.addCriteria(where(key).is(value));
        return mongoOperations.findOne(query, clazz);
    }

    public List<T> findByPropertyWithProjection(final String database,Class<T> clazz, String key, Object value, List<String> projections) {
        Query query = new Query();
        if (!CollectionUtils.isEmpty(projections))
            projections.forEach(project -> query.fields().include(project));
        query.addCriteria(where(key).is(value));
        return find(database,query, clazz);
    }

    public List<T> find(final String database,Query query, Class<T> clazz)  {
        final MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.find(query, clazz);
    }

    public T findOne(final String database,Query query, Class<T> clazz)  {
        final MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.findOne(query, clazz);
    }

    public List<T> findByProperty(final String database,String key, Object value, Class<T> clazz)  {
        final Query query = new Query().addCriteria(Criteria.where(key).is(value));
        log.info("[findByProperty] Query {}", query);
        return find(database,query, clazz);
    }

    public long count(final String database,Query query, Class<T> clazz)  {
        MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.count(query, clazz);
    }

    public void persist(T t,final String database)  {
        MongoOperations mongoOperations = getMongoOperations(database);
        mongoOperations.save(t);
    }

    public T persistAndGetUpdated(final String database,T t) {
        MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.save(t);
    }

    public void persistAll(final String database,List<T> objects, Class<T> clazz) {
        BulkOperations bulkOperations = getMongoOperations(database).bulkOps(UNORDERED, clazz);
        bulkOperations.insert(objects);
        bulkOperations.execute();
    }

    public void save(final String database,Object object)  {
        MongoOperations mongoOperations = getMongoOperations(database);
        mongoOperations.save(object);
    }

    public T updateProperties(final String database,Class<T> clazz, String key, Object value, Map<String, Object> propertiesMap)  {

        Query query = new Query();
        if (!CollectionUtils.isEmpty(propertiesMap)) {
            query.addCriteria(where(key).is(value));
            Iterator itr = propertiesMap.entrySet().iterator();
            Update multipleUpdates = new Update();
            while (itr.hasNext()) {
                Map.Entry pairs = (Map.Entry) itr.next();
                String updatePropertyKey = (String) pairs.getKey();
                multipleUpdates.set(updatePropertyKey, pairs.getValue());
            }

            FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
            findAndModifyOptions.returnNew(true);
            return findAndModify(database,query, multipleUpdates, findAndModifyOptions, clazz);
        }
        return null;
    }

    protected void applyPagination(Query query, Integer skip, Integer limit) {
        if (skip != null) {
            query.skip(skip);
        }
        if (limit != null) {
            query.limit(limit);
        }
    }



    public T findAndModify(final String database,Query query, Update update, FindAndModifyOptions options, Class<T> clazz)  {
        MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.findAndModify(query, update, options, clazz);
    }



    public T findAndRemove(final String database,Query query,  Class<T> clazz) {
        try {
            MongoOperations mongoOperations = getMongoOperations(database);
            return mongoOperations.findAndRemove(query, clazz);
        }
        catch (Exception e){
            log.info("Exception in findAndRemove:"+e);
        }
        return null;
    }

    public T updateProperty(final String database,Class<T> clazz, String key, Object value, String updateKey, Object updateValue)  {
        Query query = new Query();
        query.addCriteria(new Criteria(key).is(value));
        Update update = new Update();
        update.set(updateKey, updateValue);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(true);
        return findAndModify(database,query, update, findAndModifyOptions, clazz);
    }

    public T updatePropertiesOnConditions(final String database,Class<T> clazz, Map<String, Object> conditionsMap, Map<String, Object> propertiesMap)  {
        Query query = new Query();
        if (!CollectionUtils.isEmpty(conditionsMap) && !CollectionUtils.isEmpty(propertiesMap)) {
            Iterator conditionIterator = conditionsMap.entrySet().iterator();
            while (conditionIterator.hasNext()) {
                Map.Entry pairs = (Map.Entry) conditionIterator.next();
                query.addCriteria(where(pairs.getKey().toString()).is(pairs.getValue()));
            }

            Iterator propertiesIterator = propertiesMap.entrySet().iterator();
            Update multipleUpdates = new Update();
            while (propertiesIterator.hasNext()) {
                Map.Entry pairs = (Map.Entry) propertiesIterator.next();
                multipleUpdates.set(pairs.getKey().toString(), pairs.getValue());
            }

            FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
            findAndModifyOptions.returnNew(true);
            return findAndModify(database,query, multipleUpdates, findAndModifyOptions, clazz);
        }
        return null;
    }

    public T updateMulti(final String database,Class<T> clazz, Query query, Update update) throws Exception{
        try{
            MongoOperations mongoOperations = getMongoOperations(database);
            mongoOperations.updateMulti(query, update, clazz);
        }catch (Exception e){
            throw new Exception(e);
        }
        return null;
    }

    public DeleteResult remove(final String database,Query query, Class<T> clazz) {
        MongoOperations mongoOperations = getMongoOperations(database);
        return mongoOperations.remove(query, clazz);
    }
}
