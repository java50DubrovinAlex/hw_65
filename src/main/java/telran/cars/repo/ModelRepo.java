package telran.cars.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.cars.dto.ModelNameAmount;
import telran.cars.service.model.*;

public interface ModelRepo extends JpaRepository<Model, ModelYear> {
//@Query(value="select model_name from cars join trade_deals td"
//		+ " on cars.car_number=td.car_number group by model_name "
//		+ " having count(*) = (select max(count) from "
//		+ "(select count(*) as count from cars join trade_deals "
//		+ " on cars.car_number = trade_deals.car_number "
//		+ "group by model_name)) ", nativeQuery=true) //just SQL query
	
@Query("""
		select car.model.modelYear.name as name, count(*) as amount from Car c, TradeDeal td where c.number = td.car.number 
		group by name having count(*) = 
		(SELECT MAX(subquery.count)  FROM 
		( SELECT COUNT(*) AS count FROM Car c, TradeDeal td WHERE c.number = td.car.number GROUP BY c.model.modelYear.name) subquery)
		""")
	List<String> findMostSoldModelNames();
/*************************************************************/

@Query("""
		select  car.model.modelYear.name as name, count(*) as amount from Car car group by name order by amount desc limit :nModels
		""")
List<ModelNameAmount> findMostPopularModelNames(int nModels);
/*************************************************************************/
@Query(value="select model_name as name, count(*) as amount "
		+ "from (select * from car_owners where birth_date between :birthDate1 and :birthDate2)"
		+ " owners_age"
		+ " join cars where id=owner_id "
		+ "group by model_name order by count(*) desc limit :nModels", nativeQuery=true)
//@Query("""
//		select car.model.modelYear.name as name, count(*) as amount from Car car where car.carOwner.birthDate
//		between :birthDate1 and :birthDate2 group by name order by amount limit :nModel
//		""")
List<ModelNameAmount> findPopularModelNameOwnerAges(int nModels,
		LocalDate birthDate1, LocalDate birthDate2);

}
