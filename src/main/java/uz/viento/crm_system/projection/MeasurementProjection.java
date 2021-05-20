package uz.viento.crm_system.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.viento.crm_system.entity.Measurement;

@Projection(name = "measurementProjection", types = {Measurement.class})
public interface MeasurementProjection {

    Long getId();

    String getMeasureName();
}
