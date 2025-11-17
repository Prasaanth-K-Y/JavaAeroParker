package com.demo.ecommerce.dao;

import com.demo.ecommerce.model.Item;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.*;
import java.util.List;

@Repository
public class ItemDaoImpl implements ItemDao {

    private final DSLContext dsl;
    private final String TABLE = "springitems";

    public ItemDaoImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public boolean exists(String id) {
        Integer count = dsl.selectCount()
                .from(table(TABLE))
                .where(field("item_id").eq(id))
                .fetchOne(0, int.class);

        return count != null && count > 0;
    }

    @Override
    public Item findById(String id) {
        Record record = dsl.select()
                .from(TABLE)
                .where(field("item_id").eq(id))
                .fetchOne();

        return record == null ? null : record.into(Item.class);
    }

    @Override
    public Item save(Item item) {
        dsl.insertInto(table(TABLE))
                .set(field("item_id"), item.getItemId())
                .set(field("item_name"), item.getItemName())
                .set(field("quantity"), item.getQuantity())
                .set(field("price"), item.getPrice())
                .execute();
        return item;
    }

    @Override
    public Item update(Item item) {
        dsl.update(table(TABLE))
                .set(field("item_name"), item.getItemName())
                .set(field("quantity"), item.getQuantity())
                .set(field("price"), item.getPrice())
                .where(field("item_id").eq(item.getItemId()))
                .execute();
        return item;
    }

    @Override
    public List<Item> findAll() {
        return dsl.select()
                .from(TABLE)
                .fetchInto(Item.class);
    }
}
