package com.isai.gym.app.repository;

import com.isai.gym.app.entities.Producto;
import com.isai.gym.app.enums.CategoriaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    Optional<Producto> findByCodigoBarras(String codigoBarras);

    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.categoria) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + // <-- ¡CAMBIO AQUÍ!
            "LOWER(p.codigoBarras) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.proveedor) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Producto> buscarProductosPorKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Producto> findByActivoTrue(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (" +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.categoria) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + // <-- ¡CAMBIO AQUÍ!
            "LOWER(p.codigoBarras) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.proveedor) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Producto> buscarProductosActivosPorKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Producto> findByCategoriaAndActivoTrue(CategoriaProducto categoria, Pageable pageable);

    // Encuentra todos los productos activos
    List<Producto> findByActivoTrue();

    // Encuentra productos activos por categoría
    List<Producto> findByActivoTrueAndCategoria(CategoriaProducto categoria);

    // Busca productos activos por nombre (case-insensitive)
    List<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);

    // Busca productos activos por nombre o descripción (paginado)
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Producto> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    // Obtener un producto activo por su ID
    Optional<Producto> findByIdAndActivoTrue(Long id);
}
