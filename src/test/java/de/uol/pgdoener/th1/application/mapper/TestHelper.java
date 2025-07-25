package de.uol.pgdoener.th1.application.mapper;

import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import org.junit.platform.commons.support.ReflectionSupport;

import java.util.List;

class TestHelper {

    static List<Class<?>> listStructureClasses() {
        return ReflectionSupport.findAllClassesInPackage(
                Structure.class.getPackageName(),
                Structure.class::isAssignableFrom,
                className -> !className.equals(Structure.class.getName())
        );
    }

}
