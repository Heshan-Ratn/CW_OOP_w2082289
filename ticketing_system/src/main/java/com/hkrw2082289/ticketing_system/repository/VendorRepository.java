package com.hkrw2082289.ticketing_system.repository;

import com.hkrw2082289.ticketing_system.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {
    boolean existsByVendorId(String vendorId);

//    boolean existsByVendorIdAndPassword(String vendorId, String password);

    Vendor findByVendorIdAndPassword(String vendorId, String password);
}