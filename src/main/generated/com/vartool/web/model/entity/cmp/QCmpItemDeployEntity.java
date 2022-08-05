package com.vartool.web.model.entity.cmp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCmpItemDeployEntity is a Querydsl query type for CmpItemDeployEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCmpItemDeployEntity extends EntityPathBase<CmpItemDeployEntity> {

    private static final long serialVersionUID = -147865525L;

    public static final QCmpItemDeployEntity cmpItemDeployEntity = new QCmpItemDeployEntity("cmpItemDeployEntity");

    public final QCmpEntity _super = new QCmpEntity(this);

    public final StringPath buildScript = createString("buildScript");

    //inherited
    public final StringPath cmpId = _super.cmpId;

    //inherited
    public final StringPath cmpType = _super.cmpType;

    public final StringPath dependencyPath = createString("dependencyPath");

    public final StringPath deployPath = createString("deployPath");

    //inherited
    public final StringPath description = _super.description;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public final StringPath scmId = createString("scmId");

    public final StringPath scmPw = createString("scmPw");

    public final StringPath scmType = createString("scmType");

    public final StringPath scmUrl = createString("scmUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public final BooleanPath useDeployPath = createBoolean("useDeployPath");

    public QCmpItemDeployEntity(String variable) {
        super(CmpItemDeployEntity.class, forVariable(variable));
    }

    public QCmpItemDeployEntity(Path<? extends CmpItemDeployEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCmpItemDeployEntity(PathMetadata metadata) {
        super(CmpItemDeployEntity.class, metadata);
    }

}

