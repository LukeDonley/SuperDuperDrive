package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private NoteMapper noteMapper;
    private UserMapper userMapper;

    public NoteService(NoteMapper noteMapper, UserMapper userMapper) {
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
    }

    public void addNote(Note note) {
        this.noteMapper.insert(note);
    }

    public List<Note> getNotes(Integer userId) {
        return this.noteMapper.getNotesForUser(userId);
    }

    public Boolean deleteNote(Integer noteId) {
        this.noteMapper.delete(noteId);
        return true;
    }

    public void updateNote(Note note) {
        this.noteMapper.update(note);
    }

}
