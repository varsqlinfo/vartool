package com.vartool.web.model.entity.cmp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCmpEntity is a Querydsl query type for CmpEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCmpEntity extends EntityPathBase<CmpEntity> {

    private static final long serialVersionUID = 1384846161L;

    public static final QCmpEntity cmpEntity = new QCmpEntity("cmpEntity");

    public final com.vartool.web.model.entity.base.QAabstractAuditorModel _super = new com.vartool.web.model.entity.base.QAabstractAuditorModel(this);

    public final StringPath cmpId = createString("cmpId");

    public final StringPath cmpType = createString("cmpType");

    public final StringPath description = createString("description");

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public QCmpEntity(String variable) {
        super(CmpEntity.class, forVariable(variable));
    }

    public QCmpEntity(Path<? extends CmpEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCmpEntity(PathMetadata metadata) {
        super(CmpEntity.class, metadata);
    }

}

