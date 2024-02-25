package com.usg.chat.application.service;

import com.usg.chat.adapter.out.persistence.entity.ChatRoom.ChatRoomEntity;
import com.usg.chat.adapter.out.persistence.entity.ChatRoom.ChatRoomRepository;
import com.usg.chat.application.port.in.ChatRoom.*;
import com.usg.chat.application.port.out.ChatRoomPersistencePort;
import com.usg.chat.application.port.out.MemberPersistencePort;
import com.usg.chat.domain.Chat;
import com.usg.chat.domain.ChatRoom;
import com.usg.chat.util.SortedStringEditor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class ChatRoomService implements ChatRoomUseCase , GetChatRoomsUseCase, FindChatRoomUseCase{
    private ChatRoomPersistencePort chatRoomPersistencePort;
    private MemberPersistencePort memberPersistencePort;

    // 채팅방 생성 서비스
    @Override
    @Transactional
    public Long createChatRoom(ChatRoomCommand command) {
        // 센더와 리시버 찾음.
        Long senderId = memberPersistencePort.getIdByEmail(command.getSenderEmail());
        Long receiverId = memberPersistencePort.getIdByEmail(command.getReceiverEmail());

        ChatRoom chat = commandToChatRoom(senderId,receiverId);
        Long savedChatRoomId = chatRoomPersistencePort.createChatRoom(chat);

        return savedChatRoomId;
    }
    private ChatRoom commandToChatRoom(Long senderId, Long receiverId){
        return ChatRoom.builder()
                .senderAndReceiver(SortedStringEditor.createSortedString(senderId,receiverId))
                .build();
    }

    @Override
    public List<GetChatRoomsRes> findChatRooms(String email){
        //로그인 한 회원 찾기
        Long memberId = memberPersistencePort.getIdByEmail(email);
        List<ChatRoom> chatRooms = chatRoomPersistencePort.findChatRooms(memberId);
        return chatRooms.stream()
                .map(chatRoom -> GetChatRoomsRes.builder()
                        .chatRoomId(chatRoom.getChatRoomId()) // ChatRoom 객체에서 ID만 가져와서 설정
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Long FindChatRoom(FindChatRoomCommand command){
        Long senderId = memberPersistencePort.getIdByEmail(command.getSenderEmail());
        Long receiverId = memberPersistencePort.getIdByEmail(command.getReceiverEmail());

        ChatRoomEntity findSenderAndReceiver = chatRoomPersistencePort.findBySenderAndReceiver(senderId,receiverId);

        if (findSenderAndReceiver == null) {
            ChatRoomCommand newChatRoomCommand = ChatRoomCommand.builder()
                    .senderEmail(command.getSenderEmail())
                    .receiverEmail(command.getReceiverEmail())
                    .build();
            createChatRoom(newChatRoomCommand); // 새로운 채팅방 생성
            findSenderAndReceiver = chatRoomPersistencePort.findBySenderAndReceiver(senderId, receiverId); // 다시 조회
        }

        Long findChatRoom = findSenderAndReceiver.getRoomId();
        return findChatRoom;
    }
}
