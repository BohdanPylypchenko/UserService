package b314.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring security jwt filter implementation
 */
class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Main method for filter functionality
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Getting token from request
            String jwt = getJwtFromRequest(request);

            // Checking
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Getting username from token
                String username = tokenProvider.getUserUsernameFromJWT(jwt);

                // Loading user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Authenticating
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Filtering
        filterChain.doFilter(request, response);
    }

    /**
     * Parses request to get JWT token
     * @param request Request to parse
     * @return
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // Getting header
        String bearerToken = request.getHeader("Authorization");

        // Checking
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Returning
            return bearerToken.substring(7);
        } else {
            // No JWT in request, return null
            return null;
        }
    }

}
