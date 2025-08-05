package com.sharemgt.fullstackshareapp.controller;

import com.sharemgt.fullstackshareapp.dto.UpdateMarketPriceRequestDTO;
import com.sharemgt.fullstackshareapp.entity.Share;
import com.sharemgt.fullstackshareapp.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/share")
@CrossOrigin(origins = "http://localhost:5173")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @PostMapping("/addShare")
    public ResponseEntity<Share> addEmployee(@RequestBody Share share){
        return new ResponseEntity<>(shareService.saveShare(share), HttpStatus.CREATED);
    }

    @GetMapping("/getAllShares")
    public ResponseEntity<List<Share>> getAllEmployees(){
        return new ResponseEntity<>(shareService.getAllShares(), HttpStatus.OK);
    }

    @GetMapping("/getShareById/{shareId}")
    public ResponseEntity<Share> getEmployeeById(@PathVariable int shareId){
        return new ResponseEntity<>(shareService.getShareById(shareId), HttpStatus.OK);
    }

    @PutMapping("/updateShareMarketPrice")
    public ResponseEntity<Share> updateEmployeeSalary(@RequestBody UpdateMarketPriceRequestDTO requestDTO){
        return new ResponseEntity<>(shareService.updateShareMarketPriceById(requestDTO.getShareId(),requestDTO.getMarketPrice()), HttpStatus.OK);
    }

    @DeleteMapping("/deleteShare/{shareId}")
    public ResponseEntity<Map<String,String>> deleteEmployee(@PathVariable int shareId){
        shareService.deleteShare(shareId);
        Map<String,String> message = new HashMap<>();
        message.put("message", "Share with "+shareId+" deleted successfully!");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
