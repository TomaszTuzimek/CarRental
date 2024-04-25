package pl.tdelektro.CarRental.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.tdelektro.CarRental.Customer.Customer;
import pl.tdelektro.CarRental.Customer.CustomerFacade;

@Service
@RequiredArgsConstructor
class AuthenticateService {

    private final CustomerFacade customerFacade;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    AuthenticationResponse register(RegisterRequest request) {

        //customerFacade.findCustomer(new Customer(request.getName(), request.getEmailAddress(), request.getPassword()));
        var customer = customerFacade.addNewCustomerWithData(
                request.getName(),
                request.getEmailAddress(),
                passwordEncoder.encode(request.getPassword()),
                "USER"
        );

        var jwtToken = jwtService.generateToken(customer);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailAddress(), request.getPassword()
                )
        );
        UserDetails customer = customerFacade.findCustomerForUserDetails(request.getEmailAddress());
        var jwtToken = jwtService.generateToken(customer);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}