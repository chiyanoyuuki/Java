package com.tempo.test;

import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class IndiceController 
{
	@Autowired
	private IndiceDaoImpl indiceDao;
	
	@GetMapping(value = "/Indices", produces = "application/json")
    public ResponseEntity<List<Indice>> getIndices(@RequestParam String type) 
	{
		List<Indice> indices = indiceDao.getIndices(type);
        return new ResponseEntity<List<Indice>>(indices,HttpStatus.OK);
    }
	
	@GetMapping(value = "/IndicesVals", produces = "application/json")
    public ResponseEntity<List<IndiceVal>> getIndicesVals(@RequestParam int ID, @RequestParam String TIME, @RequestParam String type) 
	{
		List<IndiceVal> indices = indiceDao.getIndicesVals(ID, TIME,type);
        return new ResponseEntity<List<IndiceVal>>(indices,HttpStatus.OK);
    }
	
	@GetMapping(value = "/IndicesNewVals", produces = "application/json")
    public ResponseEntity<List<IndiceVal>> getIndicesNewVals(@RequestParam int ID, @RequestParam String DATE, @RequestParam String type) 
	{
		List<IndiceVal> indices = indiceDao.getIndicesNewVals(ID,DATE,type);
        return new ResponseEntity<List<IndiceVal>>(indices,HttpStatus.OK);
    }
	
	@GetMapping(value = "/IndicesTotal", produces = "application/json")
    public ResponseEntity<List<String>> getIndicesTotal(@RequestParam int ID, @RequestParam String type) 
	{
		List<String> indices = indiceDao.getIndicesTotal(ID,type);
        return new ResponseEntity<List<String>>(indices,HttpStatus.OK);
    }
	
	@GetMapping(value = "/Pays", produces = "application/json")
    public ResponseEntity<String> getPays(@RequestParam String type) 
	{
		String indices = indiceDao.getPays(type);
        return new ResponseEntity<String>(indices,HttpStatus.OK);
    }
}
