package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.viento.crm_system.entity.Measurement;
import uz.viento.crm_system.projection.MeasurementProjection;

@RepositoryRestResource(path = "measurement",collectionResourceRel = "list",excerptProjection = MeasurementProjection.class)
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
}
