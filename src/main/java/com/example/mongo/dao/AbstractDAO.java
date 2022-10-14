package com.example.mongo.dao;

import com.example.mongo.config.MongoConfig;
import com.mongodb.client.result.DeleteResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
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


    protected final MongoOperations getMongoOperations() {
        return mongoConfig.getMongoTemplate("Product");
    }


    public T findById(Class<T> clazz, Object id)  {
        final MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.findById(id, clazz);
    }

    public List<T> findAll(Class<T> clazz) {
        final MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.findAll(clazz);
    }

    public T findOneByProperty(Class<T> clazz, String key, String value)  {
        final Query query = new Query();
        query.addCriteria(where(key).is(value));
        final MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.findOne(query, clazz);
    }

    public List<T> findListByProperties(Class<T> clazz, Map<String, Object> properties)  {
        List<T> list;
        MongoOperations mongoOperations = getMongoOperations();

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

    public T findOneByProperties(Class<T> clazz, Map<String, Object> properties)  {
        List<T> byProperties = findListByProperties(clazz, properties);
        if (!CollectionUtils.isEmpty(byProperties)) {
            return byProperties.get(0);
        }
        return null;
    }

    public T findOneByPropertyWithProjection(Class<T> clazz, String key, Object value, List<String> projection) {
        MongoOperations mongoOperations = getMongoOperations();
        Query query = new Query();
        if (!CollectionUtils.isEmpty(projection)) {
            for (String projectionField : projection) {
                query.fields().include(projectionField);
            }
        }
        query.addCriteria(where(key).is(value));
        return mongoOperations.findOne(query, clazz);
    }

    public List<T> findByPropertyWithProjection(Class<T> clazz, String key, Object value, List<String> projections) {
        Query query = new Query();
        if (!CollectionUtils.isEmpty(projections))
            projections.forEach(project -> query.fields().include(project));
        query.addCriteria(where(key).is(value));
        return find(query, clazz);
    }

    public List<T> find(Query query, Class<T> clazz)  {
        final MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.find(query, clazz);
    }

    public T findOne(Query query, Class<T> clazz)  {
        final MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.findOne(query, clazz);
    }

    public List<T> findByProperty(String key, Object value, Class<T> clazz)  {
        final Query query = new Query().addCriteria(Criteria.where(key).is(value));
        log.info("[findByProperty] Query {}", query);
        return find(query, clazz);
    }

    public long count(Query query, Class<T> clazz)  {
        MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.count(query, clazz);
    }

    public void persist(T t)  {
        MongoOperations mongoOperations = getMongoOperations();
        mongoOperations.save(t);
    }

    public T persistAndGetUpdated(T t) {
        MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.save(t);
    }

    public void persistAll(List<T> objects, Class<T> clazz) {
        BulkOperations bulkOperations = getMongoOperations().bulkOps(UNORDERED, clazz);
        bulkOperations.insert(objects);
        bulkOperations.execute();
    }

    public void save(Object object)  {
        MongoOperations mongoOperations = getMongoOperations();
        mongoOperations.save(object);
    }

    public T updateProperties(Class<T> clazz, String key, Object value, Map<String, Object> propertiesMap)  {

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
            return findAndModify(query, multipleUpdates, findAndModifyOptions, clazz);
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



    public T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> clazz)  {
        MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.findAndModify(query, update, options, clazz);
    }



    public T findAndRemove(Query query,  Class<T> clazz) {
        try {
            MongoOperations mongoOperations = getMongoOperations();
            return mongoOperations.findAndRemove(query, clazz);
        }
        catch (Exception e){
            log.info("Exception in findAndRemove:"+e);
        }
        return null;
    }

    public T updateProperty(Class<T> clazz, String key, Object value, String updateKey, Object updateValue)  {
        Query query = new Query();
        query.addCriteria(new Criteria(key).is(value));
        Update update = new Update();
        update.set(updateKey, updateValue);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(true);
        return findAndModify(query, update, findAndModifyOptions, clazz);
    }

    public T updatePropertiesOnConditions(Class<T> clazz, Map<String, Object> conditionsMap, Map<String, Object> propertiesMap)  {
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
            return findAndModify(query, multipleUpdates, findAndModifyOptions, clazz);
        }
        return null;
    }

    public T updateMulti(Class<T> clazz, Query query, Update update) throws Exception{
        try{
            MongoOperations mongoOperations = getMongoOperations();
            mongoOperations.updateMulti(query, update, clazz);
        }catch (Exception e){
            throw new Exception(e);
        }
        return null;
    }

    public DeleteResult remove(Query query, Class<T> clazz) {
        MongoOperations mongoOperations = getMongoOperations();
        return mongoOperations.remove(query, clazz);
    }
}
