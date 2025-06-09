package com.isai.gym.app.repository;

import com.isai.gym.app.entities.ItemVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemVentaRepository extends JpaRepository<ItemVenta, Long> {

    List<ItemVenta> findByVentaId(Long ventaId);

}