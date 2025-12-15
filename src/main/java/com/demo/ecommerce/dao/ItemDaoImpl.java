package com.demo.ecommerce.dao;

import com.demo.ecommerce.model.Item;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Repository
public class ItemDaoImpl implements ItemDao {

    private final DSLContext dsl;
    private static final String TABLE = "springitems";

    public ItemDaoImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

  @Override
public boolean exists(Long id) {
    Integer count = dsl.selectCount()
            .from(table(TABLE))
            .where(field("item_id").eq(id))
            .fetchOne(0, int.class);

    return count != null && count > 0;
}


@Override
public Item findById(Long id) {
    Record record = dsl.select()
            .from(table(TABLE))
            .where(field("item_id").eq(id))
            .fetchOne();

    return record == null ? null : record.into(Item.class);
}

@Override
public Item findByName(String itemName) {
    Record record = dsl.select()
            .from(table(TABLE))
            .where(field("item_name").eq(itemName))
            .fetchOne();

    return record == null ? null : record.into(Item.class);
}

@Override
public Item save(Item item) {
    // Insert the item
    dsl.insertInto(table(TABLE))
        .set(field("item_name"), item.getItemName())
        .set(field("quantity"), item.getQuantity())
        .set(field("price"), item.getPrice())
        .execute();

    // Fetch the last inserted ID
    Long generatedId = dsl.select(field("LAST_INSERT_ID()", Long.class))
        .fetchOne(0, Long.class);

    // Set the generated ID on the item
    if (generatedId != null) {
        item.setItemId(generatedId);
    }

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
                .from(table(TABLE))
                .fetchInto(Item.class);
    }
}
