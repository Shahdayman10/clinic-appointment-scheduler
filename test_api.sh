#!/bin/bash

# Test script for Clinic Appointment Scheduler API

echo "Testing Clinic Appointment Scheduler API"
echo "========================================"

# Base URL
BASE_URL="http://localhost:8080/api"

# Test 1: Get all appointments (this endpoint is not implemented, but we can test the base)
echo "Test 1: Testing base API endpoint"
curl -X GET "$BASE_URL" -H "Accept: application/json"
echo -e "\n"

# Test 2: Get appointment by ID (assuming appointment with ID 1 exists)
echo "Test 2: Get appointment by ID (ID: 1)"
curl -X GET "$BASE_URL/appointments/1" -H "Accept: application/json"
echo -e "\n"

# Test 3: Get doctor availability (assuming doctor with ID 1 exists and date 2025-10-15)
echo "Test 3: Get doctor availability (Doctor ID: 1, Date: 2025-10-15)"
curl -X GET "$BASE_URL/doctor-availability?doctorId=1&date=2025-10-15" -H "Accept: application/json"
echo -e "\n"

echo "API tests completed."