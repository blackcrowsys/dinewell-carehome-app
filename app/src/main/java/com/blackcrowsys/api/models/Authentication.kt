package com.blackcrowsys.api.models

data class AuthenticationRequest(val username: String,
                                 val password: String)

data class AuthenticationResponse(val username: String,
                                  val jwtToken: String,
                                  val isAuthenticated: Boolean,
                                  val permissions: Map<String, String>)