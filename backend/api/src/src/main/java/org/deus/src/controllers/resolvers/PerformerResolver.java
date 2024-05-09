package org.deus.src.controllers.resolvers;

//import com.coxautodev.graphql.tools.GraphQLResolver;
//import lombok.RequiredArgsConstructor;
//import org.deus.src.dtos.fromModels.PerformerDTO;
//import org.deus.src.dtos.fromModels.UserDTO;
//import org.deus.src.models.auth.UserModel;
//import org.deus.src.repositories.UserRepository;
//import org.deus.src.services.PerformerService;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class PerformerResolver implements GraphQLResolver<PerformerDTO> {
//    private final UserRepository userRepository;
//
//    public UserDTO getUser(PerformerDTO performerDTO) {
//        UserModel userModel = userRepository.findById(performerDTO.getUser().getId()).orElse(null);
//        return UserModel.mapUserToDTO(userModel);
//    }
//}
