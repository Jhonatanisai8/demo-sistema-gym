package com.isai.gym.app.controllers.assetsIMG;

import com.isai.gym.app.services.impl.AlmacenArchivoImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/assest")
@RequiredArgsConstructor
public class AssestsController {
    private final AlmacenArchivoImpl almacenService;

    @GetMapping("/{filename:.+}")
    public Resource getResource(@PathVariable String filename) {
        return almacenService.cargarArchivoComoRecurso(filename);
    }
}
